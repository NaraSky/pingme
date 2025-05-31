package com.lb.pingme.config.annotation;


import com.lb.pingme.common.exception.AuthException;
import com.lb.pingme.common.helper.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

public class ValidateLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        ValidateLogin validateLogin = method.getAnnotation(ValidateLogin.class);
        if (validateLogin == null) {
            return true;
        }
        String currentUserId = SessionHelper.getCurrentUserId();
        if (StringUtils.isBlank(currentUserId)) {
            // 未登录！权限校验失败
            throw new AuthException("未登录！");
        }
        return true;
    }
}
