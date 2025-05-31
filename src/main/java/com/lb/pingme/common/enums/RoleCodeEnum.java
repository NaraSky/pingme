package com.lb.pingme.common.enums;

import lombok.Getter;

@Getter
public enum RoleCodeEnum {

    USER(1, "普通用户"),
    ADMIN(2, "管理员"),
    BLACK(3, "黑名单"),
    GROUP(4, "群组"),
    ROBOT(5, "聊天机器人"),
    PUBLIC_ACCOUNT(6, "公众号");

    private Integer code;
    private String name;

    RoleCodeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
