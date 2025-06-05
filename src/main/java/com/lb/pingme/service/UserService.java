package com.lb.pingme.service;

import com.lb.pingme.common.constants.CookieConstants;
import com.lb.pingme.common.constants.WebConstant;
import com.lb.pingme.common.enums.APIErrorCommonEnum;
import com.lb.pingme.common.enums.RedisKeyEnum;
import com.lb.pingme.common.enums.RoleCodeEnum;
import com.lb.pingme.common.enums.UserStatusEnum;
import com.lb.pingme.common.exception.BusinessException;
import com.lb.pingme.common.util.*;
import com.lb.pingme.domain.convert.UserEntryConvert;
import com.lb.pingme.domain.vo.request.CreateGroupRequestVO;
import com.lb.pingme.domain.vo.request.CreatePublicAccountRequestVO;
import com.lb.pingme.domain.vo.request.CreateRobotRequestVO;
import com.lb.pingme.domain.vo.response.UserBaseResponseInfoVO;
import com.lb.pingme.repository.dao.IGroupUserDAO;
import com.lb.pingme.repository.dao.IUserDAO;
import com.lb.pingme.repository.entity.GroupUserEntity;
import com.lb.pingme.repository.entity.UserEntity;
import com.lb.pingme.service.publicaccount.PublicAccountService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private IUserDAO userDAO;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IGroupUserDAO groupUserDAO;

    @Autowired
    private PublicAccountService publicAccountService;

    /***
     * USER ID 前缀，不同类型账号的ID前缀，便于快速识别账号类型
     */
    private static final String USER_ID_PREFIX = "U";
    private static final String ROBOT_ID_PREFIX = "R";
    private static final String GROUP_ID_PREFIX = "G";
    private static final String PUBLIC_ACCOUNT_ID_PREFIX = "P";

    /**
     * 用户注册方法
     * 使用@Transactional注解确保数据一致性，如果任何步骤失败都会回滚
     * 这里展示了事务同步回调的高级用法
     */
    @Transactional
    public boolean registry(String photo, String userName, String mobile, String password) {
        // 第一步：参数校验，尽早发现问题避免无效处理
        this.validateRegistryParam(userName, mobile);

        // 第二步：将用户信息持久化到数据库
        String uid = this.registryUser2DB(photo, userName, mobile, password);

        // 第三步：使用事务同步机制，确保在事务成功提交后刷新缓存
        // 这样做的好处是如果事务回滚，缓存就不会被错误更新
        TransactionSyncManagerUtil.registerSynchronization(() -> {
            // 刷新用户缓存，提高后续查询性能
            this.refreshAndGetUserEntityFromCache(uid);
        });
        return true;
    }

    /**
     * 从缓存中获取用户实体，如果需要则刷新缓存
     *
     * @param userId       用户ID
     * @param refreshCache 是否刷新缓存，默认为false
     * @return 返回用户实体
     */
    private UserEntity refreshAndGetUserEntityFromCache(String userId, boolean... refreshCache) {
        String key = RedisKeyEnum.USER_INFO_CACHE.getKey(userId);
        // 从Redis获取缓存的用户信息
        String val = redisService.get(key);

        // 判断是否需要强制刷新缓存
        boolean refresh = refreshCache != null && refreshCache.length > 0 && refreshCache[0];

        // 如果不需要刷新且缓存存在，直接返回缓存数据
        if (!refresh && StringUtils.isNotBlank(val)) {
            return JsonUtil.fromJson(val, UserEntity.class);
        }

        // 从数据库查询最新数据
        UserEntity userEntity = userDAO.findByUserId(userId);
        if (userEntity != null) {
            // 更新缓存，提高后续查询效率
            this.refreshUserDetailCache(userEntity);
        }
        return userEntity;
    }

    private void refreshUserDetailCache(UserEntity userEntity) {
        if (userEntity == null) {
            return;
        }
        String key = RedisKeyEnum.USER_INFO_CACHE.getKey(userEntity.getUserId());
        // 将用户实体转换为JSON字符串并设置到Redis缓存中
        redisService.set(key, JsonUtil.toJsonString(userEntity), RedisKeyEnum.USER_INFO_CACHE.getExpireTime());
    }

    private String registryUser2DB(String photo, String userName, String mobile, String password) {
        UserEntity userEntity = new UserEntity();
        String userId = IDGenerateUtil.createId(USER_ID_PREFIX);
        userEntity.setUserId(userId);
        userEntity.setUserName(StringUtil.handleSpecialHtmlTag(userName));
        userEntity.setPhoto(photo);
        userEntity.setMobile(mobile);
        userEntity.setStatus(UserStatusEnum.ENABLE.getStatus());
        userEntity.setPassword(md5Pwd(password));
        userEntity.setRoleCode(RoleCodeEnum.USER.getCode());
        userEntity.setCreateBy(userId);
        // 注册用户
        return userDAO.save(userEntity).getUserId();
    }

    private String md5Pwd(String password) {
        return MD5Utils.md5(password.concat(WebConstant.MD5_SALT));
    }

    private void validateRegistryParam(String userName, String mobile) {
        if (hasUserInfo(mobile)) {
            throw new BusinessException("用户已存在");
        }
        if (!Pattern.matches("^[0-9a-zA-Z_]{1,}$", mobile)) {
            throw new BusinessException("账号格式不合法");
        }
        if (StringUtils.isBlank(userName) || userName.length() < 2 || userName.length() > 12) {
            throw new BusinessException("用户名不合法：2~12位字符");
        }
        if (userNameIsExist(userName)) {
            throw new BusinessException("用户名已经被占用，换一个吧");
        }
    }

    private boolean hasUserInfo(String mobile) {
        return userDAO.findByMobile(mobile) != null;
    }

    private boolean userNameIsExist(String userName) {
        return userDAO.findByUserName(userName) != null;
    }

    /**
     * 创建机器人方法
     * 展示了分布式锁的使用场景：防止用户快速连续点击造成重复创建
     */
    @Transactional
    public void createRobot(CreateRobotRequestVO requestVO) {
        // 检查机器人名称是否已存在，保证唯一性
        UserEntity userEntity = userDAO.findByUserName(requestVO.getRobotName());
        Assert.isTrue(userEntity == null, "机器人账号已存在！");
        String createUserId = requestVO.getCreateUserId();
        String key = RedisKeyEnum.CREATE_ROBOT_LIMIT.getKey(createUserId);
        /**
         * 使用Redis分布式锁防止快速点击，防快速点击
         */
        boolean lockResult = redisService.installDistributedLock(key, WebConstant.CACHE_NONE, RedisKeyEnum.CREATE_ROBOT_LIMIT.getExpireTime());
        Assert.isTrue(lockResult, "创建中请稍等！请勿频繁点击");
        String robotRole = requestVO.getRobotRole();
        robotRole = StringUtils.isBlank(robotRole) ? "全能助手" : robotRole;
        this.registryRobot2DB(requestVO.getId(),
                requestVO.getRobotNumber(),
                requestVO.getRobotName(),
                robotRole, requestVO.getRobotPhoto(),
                createUserId);
    }

    private String registryRobot2DB(Long id, String robotNumber, String robotName, String role, String robotPhoto, String createUserId) {
        UserEntity userEntity;

        if (id != null) {
            userEntity = userDAO.findById(id).orElse(null);
        } else {
            userEntity = new UserEntity();
            String robotId = IDGenerateUtil.createId(ROBOT_ID_PREFIX);
            userEntity.setUserId(robotId);
            // 密码设置为用户ID, 本身也不支持登录
            userEntity.setPassword(md5Pwd(robotId));
            userEntity.setStatus(UserStatusEnum.ENABLE.getStatus());
            userEntity.setRoleCode(RoleCodeEnum.ROBOT.getCode());
            userEntity.setCreateBy(createUserId);
        }
        userEntity.setUserName(robotName);
        userEntity.setPhoto(robotPhoto);
        userEntity.setMobile(robotNumber);
        userEntity.setSignature(role);
        // 注册用户
        userEntity = userDAO.save(userEntity);
        String robotId = userEntity.getUserId();
        // 注册成功，事务结束后刷新用户缓存信息
        this.refreshAndGetUserEntityFromCache(robotId);
        return robotId;
    }

    /**
     * 创建公众号
     * <p>
     * 复用用户信息表，账号角色为 PUBLIC_ACCOUNT
     */
    @Transactional
    public void createPublicAccount(CreatePublicAccountRequestVO requestVO) {
        String account = requestVO.getAccount();
        String accountName = requestVO.getAccountName();
        String signature = requestVO.getSignature();
        String photo = requestVO.getAccountPhoto();
        String createUserId = requestVO.getCreateUserId();
        /**
         * 防快速点击
         */
        String key = RedisKeyEnum.CREATE_PUBLIC_ACCOUNT_LIMIT.getKey();
        boolean lockResult = redisService.installDistributedLock(key, WebConstant.CACHE_NONE, RedisKeyEnum.CREATE_ROBOT_LIMIT.getExpireTime());
        Assert.isTrue(lockResult, "创建中请稍等！请勿频繁点击");
        // 创建机器人
        this.registryPublicAccount2DB(account, accountName, photo, signature, createUserId);
    }

    /**
     * 创建公众号
     *
     * @return
     */
    private String registryPublicAccount2DB(String account, String accountName, String accountPhoto,
                                            String accountSignature, String createUserId) {

        UserEntity userEntity = userDAO.findByMobile(account);
        Assert.isTrue(userEntity == null, "公众号已经存在");

        userEntity = new UserEntity();
        String userId = IDGenerateUtil.createId(PUBLIC_ACCOUNT_ID_PREFIX);
        userEntity.setUserId(userId);
        userEntity.setUserName(StringUtil.handleSpecialHtmlTag(accountName));
        userEntity.setPhoto(accountPhoto);
        userEntity.setMobile(account);
        userEntity.setSignature(accountSignature);
        userEntity.setStatus(UserStatusEnum.ENABLE.getStatus());
        // 密码设置为用户ID, 本身也不支持登录
        userEntity.setPassword(md5Pwd(account));
        userEntity.setRoleCode(RoleCodeEnum.PUBLIC_ACCOUNT.getCode());
        userEntity.setCreateBy(createUserId);
        // 注册用户
        userDAO.save(userEntity);
        // 注册成功，事务结束后刷新用户缓存信息
        // 刷新用户缓存
        this.refreshAndGetUserEntityFromCache(account);
        // 刷新公众号文章列表
        publicAccountService.refreshPublicAccountListCache();
        return userId;
    }

    /**
     * 创建聊天群组的核心方法
     * 展示了复杂业务流程的处理：参数校验 -> 创建群组 -> 添加成员 -> 缓存管理
     */
    public void createChatGroup(CreateGroupRequestVO requestPram) {
        String groupName = requestPram.getGroupName();
        String createUserId = requestPram.getCreateUserId();
        Set<String> groupUserIdSet = requestPram.getUserIds();

        // 业务规则校验：群组至少需要3个成员（包括创建者）
        Assert.isTrue(CollectionUtils.isNotEmpty(groupUserIdSet) && groupUserIdSet.size() >= 2, "至少需要添加3名成员");
        // 群组默认添加创建人，群聊的创建人默认为群聊管理员
        groupUserIdSet.add(createUserId);
        // 步骤1：创建群组记录
        String groupId = this.registryGroup2DB(groupName, createUserId);
        // 步骤2：批量添加群组成员，使用批量操作提高性能
        this.addUsers2Group(groupId, groupUserIdSet, createUserId);
        // 步骤3：清理相关用户的群组缓存，保证数据一致性
        removeGroupUserIdsFromCacheByUserId(groupUserIdSet);
        // 步骤4：使用事务同步机制，在事务提交后刷新群组成员缓存
        TransactionSyncManagerUtil.registerSynchronization(() -> refreshGroupUserIdListCache(groupId));
    }

    /**
     * 创建群组到数据库
     *
     * @param groupName
     * @param createUserId
     */
    private String registryGroup2DB(String groupName, String createUserId) {
        UserEntity userEntity = new UserEntity();
        String groupId = IDGenerateUtil.createId(GROUP_ID_PREFIX);
        userEntity.setUserId(groupId);
        userEntity.setUserName(StringUtil.handleSpecialHtmlTag(groupName));
        userEntity.setPhoto("https://coderutil.oss-cn-beijing.aliyuncs.com/bbs-image/file_6b2a447e4a1f4df8870f8a51e2874618.png");
        userEntity.setMobile("-");
        userEntity.setStatus(UserStatusEnum.ENABLE.getStatus());
        // 密码设置为用户ID, 本身也不支持登录
        userEntity.setPassword(md5Pwd(groupId));
        userEntity.setRoleCode(RoleCodeEnum.GROUP.getCode());
        userEntity.setCreateBy(createUserId);
        // 创建群组
        userDAO.save(userEntity);
        // 注册成功，事务结束后刷新用户缓存信息
        TransactionSyncManagerUtil.registerSynchronization(() -> {
            // 刷新用户缓存
            this.refreshAndGetUserEntityFromCache(groupId);
        });
        return groupId;
    }

    /**
     * 批量添加用户到群组
     * 展示了批量操作的最佳实践：使用Stream API构建对象列表，然后批量保存
     */
    private void addUsers2Group(String groupId, Set<String> userIdSet, String createUserId) {
        if (CollectionUtils.isEmpty(userIdSet)) {
            return;
        }
        Date now = new Date();
        List<GroupUserEntity> groupUserEntities = userIdSet.stream().map(
                uid -> {
                    GroupUserEntity groupUserEntity = new GroupUserEntity();
                    groupUserEntity.setGroupId(groupId);
                    groupUserEntity.setUserId(uid);
                    groupUserEntity.setStatus(UserStatusEnum.ENABLE.getStatus());
                    groupUserEntity.setCreateBy(createUserId);
                    groupUserEntity.setCreateDate(now);
                    return groupUserEntity;
                }
        ).collect(Collectors.toList());
        // 批量添加用户到群组
        groupUserDAO.saveAll(groupUserEntities);
    }

    /**
     * 删除用加入的群组列表缓存
     */
    private void removeGroupUserIdsFromCacheByUserId(Set<String> userIds) {
        for (String userId : userIds) {
            String key = RedisKeyEnum.USER_ATTEND_GROUP_ID_LIST_CACHE.getKey(userId);
            redisService.remove(key);
        }
    }

    /**
     * 刷新群组用户列表缓存的方法
     * 使用Redis的有序集合(ZSet)存储群组成员，便于排序和分页
     */
    private Set<String> refreshGroupUserIdListCache(String groupId) {
        String key = RedisKeyEnum.GROUP_USER_ID_LIST_CACHE.getKey(groupId);
        // 从数据库查询群组的活跃成员
        List<GroupUserEntity> groupUserEntities =
                groupUserDAO.findAllByGroupIdAndStatusOrderByIdDesc(groupId, UserStatusEnum.ENABLE.getStatus());
        // 先清空旧缓存
        redisService.remove(key);
        if (CollectionUtils.isEmpty(groupUserEntities)) {
            return Collections.emptySet();
        }

        // 构建Redis ZSet数据结构，用ID作为分数实现排序
        Set<ZSetOperations.TypedTuple<String>> typedTupleSet = new HashSet<>();
        for (GroupUserEntity groupUser : groupUserEntities) {
            String value = groupUser.getUserId().toString();
            double score = groupUser.getId();   // 使用主键ID作为排序分数
            ZSetOperations.TypedTuple<String> tuple = new DefaultTypedTuple<>(value, score);
            typedTupleSet.add(tuple);
        }
        // 批量写入Redis，设置过期时间避免内存泄漏
        redisService.zadd(key, typedTupleSet, RedisKeyEnum.GROUP_USER_ID_LIST_CACHE.getExpireTime());
        return groupUserEntities.stream().map(GroupUserEntity::getUserId).collect(Collectors.toSet());
    }

    public UserBaseResponseInfoVO login(String mobile, String password) {
        if (!hasUserInfo(mobile)) {
            throw new BusinessException(APIErrorCommonEnum.USER_NOT_FOUND);
        }
        UserEntity userEntity = userDAO.findByMobileAndPassword(mobile, md5Pwd(password));
        if (userEntity == null) {
            throw new BusinessException("密码验证失败");
        }
        if (userEntity.getRoleCode().equals(RoleCodeEnum.BLACK.getCode())) {
            throw new BusinessException("账号异常！联系管理员解封");
        }
        UserBaseResponseInfoVO user = userLogin(userEntity);
        return user;
    }

    private UserBaseResponseInfoVO userLogin(UserEntity userEntity) {
        // 创建用户登录会话数据
        this.createLoginSession(userEntity.getUserId());
        UserBaseResponseInfoVO user = UserEntryConvert.convertBaseVo(userEntity);
        return user;
    }

    private void createLoginSession(String userId) {
        String sessionId = MD5Utils.md5(userId);
        // sessionId：UserId缓存，用的String数据结构存储，有效期3天
        String sessionKey = RedisKeyEnum.USER_SESSION_PREFIX.getKey(sessionId);
        redisService.set(sessionKey, userId, RedisKeyEnum.USER_SESSION_PREFIX.getExpireTime());
        // 种浏览器Cookie
        Cookie cookie = new Cookie(CookieConstants.C_U_USER_COOKIE_KEY, sessionId);
        cookie.setPath("/");
        cookie.setMaxAge(CookieConstants.COOKIE_OUT_TIME);
        response.addCookie(cookie);
    }

    public String getUserIdBySessionId(String sessionId) {
        String sessionKey = RedisKeyEnum.USER_SESSION_PREFIX.getKey(sessionId);
        return redisService.get(sessionKey);
    }
}
