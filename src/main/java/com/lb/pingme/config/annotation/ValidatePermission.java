package com.lb.pingme.config.annotation;

import com.lb.pingme.common.enums.RoleCodeEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ValidatePermission {

    /***
     * 需要进行校验的权限类型，默认校验管理员
     * @see RoleCodeEnum
     * @return
     */
    RoleCodeEnum[] role() default {RoleCodeEnum.ADMIN};
}
