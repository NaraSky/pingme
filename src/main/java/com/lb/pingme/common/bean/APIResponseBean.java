package com.lb.pingme.common.bean;

import lombok.Data;

@Data
public class APIResponseBean<T> {

    private Boolean success;

    private Integer code;

    private String msg;

    /***
     * 耗时
     */
    private Long cost;

    private T data;
}
