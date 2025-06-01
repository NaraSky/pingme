package com.lb.pingme.service.publicaccount;

import com.lb.pingme.common.enums.RedisKeyEnum;
import com.lb.pingme.common.enums.RoleCodeEnum;
import com.lb.pingme.common.util.JsonUtil;
import com.lb.pingme.domain.vo.response.publicaccouunt.PublicAccountVO;
import com.lb.pingme.repository.dao.IUserDAO;
import com.lb.pingme.repository.entity.UserEntity;
import com.lb.pingme.service.RedisService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicAccountService {

    @Autowired
    private IUserDAO userDAO;

    @Autowired
    private RedisService redisService;

    /**
     * 刷新公众号列表缓存
     *
     * @return
     */
    public List<PublicAccountVO> refreshPublicAccountListCache() {
        String key = RedisKeyEnum.PUBLIC_ACCOUNT_LIST_CACHE.getKey();
        List<UserEntity> list = userDAO.findAllByRoleCodeOrderByIdDesc(RoleCodeEnum.PUBLIC_ACCOUNT.getCode());
        List<PublicAccountVO> accountList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            accountList = list.stream().map(e -> {
                PublicAccountVO publicAccount = new PublicAccountVO();
                publicAccount.setId(e.getUserId());
                publicAccount.setName(e.getUserName());
                publicAccount.setPhoto(e.getPhoto());
                return publicAccount;
            }).collect(Collectors.toList());
        }
        redisService.set(key, JsonUtil.toJsonString(accountList), RedisKeyEnum.PUBLIC_ACCOUNT_LIST_CACHE.getExpireTime());
        return accountList;
    }
}
