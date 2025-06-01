package com.lb.pingme.config.annotation;

import com.lb.pingme.common.enums.ClickEvent;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface SafeClick {

    /***
     * 事件KEY
     * @return
     */
    ClickEvent event();

    /***
     * 默认500ml内只能允许一次点击操作，单位：毫秒
     * @return
     */
    long time() default 500L;

    /***
     * 异常文案提示
     * @return
     */
    String message() default "点击太频繁，休息会吧";
}
