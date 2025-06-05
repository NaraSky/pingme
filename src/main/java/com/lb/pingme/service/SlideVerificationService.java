package com.lb.pingme.service;

import com.lb.pingme.common.enums.RedisKeyEnum;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class SlideVerificationService {

    @Autowired
    private RedisService redisService;

    public void validateTokenResult(String id) {
        String tokenKey = RedisKeyEnum.SLIDE_VERIFICATION_TOKEN_VALIDATE_RESULT_CACHE.getKey(id);
        String cache = redisService.get(tokenKey);
        Assert.isTrue(ObjectUtils.equals(cache, "1"), "滑块验证失败");
    }

}
