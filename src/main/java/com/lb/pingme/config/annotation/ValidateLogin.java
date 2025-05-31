package com.lb.pingme.config.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解实现:校验是否登录
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ValidateLogin {
}
