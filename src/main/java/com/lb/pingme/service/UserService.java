package com.lb.pingme.service;

import com.lb.pingme.common.constants.WebConstant;
import com.lb.pingme.common.enums.RedisKeyEnum;
import com.lb.pingme.common.enums.RoleCodeEnum;
import com.lb.pingme.common.enums.UserStatusEnum;
import com.lb.pingme.common.exception.BusinessException;
import com.lb.pingme.common.util.IDGenerateUtil;
import com.lb.pingme.common.util.JsonUtil;
import com.lb.pingme.common.util.MD5Utils;
import com.lb.pingme.common.util.StringUtil;
import com.lb.pingme.domain.vo.request.CreateRobotRequestVO;
import com.lb.pingme.repository.dao.IUserDAO;
import com.lb.pingme.repository.entity.UserEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private IUserDAO userDAO;

    @Autowired
    private RedisService redisService;

    /***
     * USER ID 前缀
     */
    private static final String USER_ID_PREFIX = "U";

    public boolean registry(String photo, String userName, String mobile, String password) {
        // 校验注册参数
        this.validateRegistryParam(userName, mobile);
        // 将用户信息注册到数据库，并返回用户ID
        String uid = this.registryUser2DB(photo, userName, mobile, password);
        // 刷新并从缓存中获取用户实体
        this.refreshAndGetUserEntityFromCache(uid);
        return true;
    }

    /**
     * 从缓存中获取用户实体，如果需要则刷新缓存
     * @param userId 用户ID
     * @param refreshCache 是否刷新缓存，默认为false
     * @return 返回用户实体
     */
    private UserEntity refreshAndGetUserEntityFromCache(String userId, boolean... refreshCache) {
        String key = RedisKeyEnum.USER_INFO_CACHE.getKey(userId);
        // 从缓存中获取用户信息
        String val = redisService.get(key);
        // 如果不需要刷新缓存且缓存中有值，则直接从缓存中获取用户实体
        boolean refresh = refreshCache != null && refreshCache.length > 0 && refreshCache[0];
        if (!refresh && StringUtils.isNotBlank(val)) {
            return JsonUtil.fromJson(val, UserEntity.class);
        }
        UserEntity userEntity = userDAO.findByUserId(userId);
        // 如果用户实体不为空，则刷新缓存
        if (userEntity != null) {
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


}
