package com.lb.pingme.service;

import com.lb.pingme.common.constants.WebConstant;
import com.lb.pingme.common.exception.AuthException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidCodeService {

    @Autowired
    private SessionService sessionService;

    public void validCode(String picCheckCode) {
        if (StringUtils.isBlank(picCheckCode)) {
            throw new AuthException("图形校验码验证失败");
        }
        // 如果用户输入的验证码与会话中的验证码一致，移除会话中的验证码
        if (picCheckCode.equals(sessionService.get(WebConstant.PIC_VALID_CODE_SESSION_KEY))) {
            // 验证成功后立即删除验证码，防止重复使用
            sessionService.remove(WebConstant.PIC_VALID_CODE_SESSION_KEY);
            return;
        }
        throw new AuthException("图形校验码验证失败");
    }
}
