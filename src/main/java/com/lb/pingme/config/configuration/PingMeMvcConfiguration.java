package com.lb.pingme.config.configuration;

import com.lb.pingme.config.annotation.ValidateLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PingMeMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册登录核验拦截器,拦截所有 /api 前缀的http请求
        registry.addInterceptor(validateLoginInterceptor()).addPathPatterns("/api/**");
    }

    @Bean
    public ValidateLoginInterceptor validateLoginInterceptor() {
        return new ValidateLoginInterceptor();
    }
}
