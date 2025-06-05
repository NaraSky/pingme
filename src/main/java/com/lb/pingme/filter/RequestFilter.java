package com.lb.pingme.filter;

import com.lb.pingme.common.constants.CookieConstants;
import com.lb.pingme.common.helper.SessionHelper;
import com.lb.pingme.common.util.RemoteIpUtil;
import com.lb.pingme.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.io.IOException;

@Slf4j
// order值越小，优先级越高
// 拦截所有请求
// asyncSupported 配置是给sse异步响应的支持
@Order(-6)
@WebFilter(filterName = "requestFilter", urlPatterns = {"/*"}, asyncSupported = true)
public class RequestFilter implements Filter {

    @Autowired
    private UserService userService;

    /***
     * 未登录
     */
    private static final String DEFAULT_USER_ID = "访客";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // 记录开始请求时间
        long startTime = System.currentTimeMillis();

        // 请求资源URI
        String uri = request.getRequestURI();
        // 获取UA信息
        String userAgent = request.getHeader("User-Agent");
        // 获取客户端IP
        String ip = RemoteIpUtil.getRemoteIpByRequest(request);
        // 任务id
        String taskId = request.getHeader("task-id");

        // 取集中session服务判断是否已经登录，如果已经登录则直接进入后续方法。
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.info("request in =====> Request-Uri: {}, User-ID: {} , Client-IP: {}", uri, DEFAULT_USER_ID, ip);
            SessionHelper.setClientUserInfo(null, ip, taskId);
            filterChain.doFilter(servletRequest, servletResponse);
            log.info("request out =====> Request-Uri: {}, User-ID: {} , Client-IP: {}, Cost-Time: {}ms", uri, DEFAULT_USER_ID, ip, System.currentTimeMillis() - startTime);
            return;
        }
        String userId = null;
        // 获取当前登录用户信息
        for (Cookie cookie : cookies) {
            // C_U_USER_COOKIE_KEY对cookie为登录时种的cookie name，value为登录时生成的sessionId，跟用户userId对应，缓存在redis中
            if (CookieConstants.C_U_USER_COOKIE_KEY.equals(cookie.getName()) &&
                    StringUtils.isNotBlank(cookie.getValue())) {
                // 从redis中查询sessionId对应的用户Id
                userId = userService.getUserIdBySessionId(cookie.getValue());
            }
        }
        try {
            SessionHelper.setClientUserInfo(userId, ip, taskId);
            String logUserId = userId == null ? DEFAULT_USER_ID : userId;
            log.info("request in =====> Request-Uri: {}, User-ID: {} , Client-IP: {}", uri, logUserId, ip);
            filterChain.doFilter(servletRequest, servletResponse);
            log.info("request out =====> Request-Uri: {}, User-ID: {} , Client-IP: {}, Cost-Time: {}ms", uri, logUserId, ip, System.currentTimeMillis() - startTime);
        } finally {
            SessionHelper.clear();
        }
    }

    @Override
    public void destroy() {
    }
}
