package com.lb.pingme.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ArticleStatusEnum {

    wait_push(1, "待推送"),
    pushed(2, "已经推送");

    private Integer status;
    private String statusName;

}
