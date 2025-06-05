package com.lb.pingme.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum CommonStatusEnum {

    NEW(0, "新建"),
    PUBLISHED(1, "发布"),
    BACK(2, "驳回"),
    DELETED(3, "删除"),
    WAIT_REVIEW(4, "待审核"),
    FINISHED(5, "已完成");

    private Integer statusCode;
    private String statusDesc;

    CommonStatusEnum(Integer statusCode, String statusDesc) {
        this.statusCode = statusCode;
        this.statusDesc = statusDesc;
    }

    public String getStatus() {
        return this.name();
    }

    public static String getStatusDescByStatus(String status) {
        for (CommonStatusEnum commonStatusEnum : CommonStatusEnum.values()) {
            if (commonStatusEnum.name().equals(status)) {
                return commonStatusEnum.getStatusDesc();
            }
        }
        return null;
    }

    public static String getStatusDescByStatus(Integer statusCode) {
        for (CommonStatusEnum commonStatusEnum : CommonStatusEnum.values()) {
            if (commonStatusEnum.statusCode.equals(statusCode)) {
                return commonStatusEnum.getStatusDesc();
            }
        }
        return null;
    }

    public static List<String> getAllEnableStatusDesc() {
        List<String> statusList = new ArrayList<>();
        statusList.add(NEW.getStatus());
        statusList.add(PUBLISHED.getStatus());
        statusList.add(BACK.getStatus());
        statusList.add(WAIT_REVIEW.getStatus());
        return statusList;
    }

    public static List<String> getAllStatusDesc() {
        List<String> statusList = getAllEnableStatusDesc();
        statusList.add(DELETED.getStatus());
        return statusList;
    }

    public static List<String> getAllowSubmitStatus() {
        List<String> statusList = new ArrayList<>();
        statusList.add(NEW.getStatus());
        statusList.add(WAIT_REVIEW.getStatus());
        return statusList;
    }
}
