package com.lb.pingme.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ClickEvent {


    RESOURCE_SUPPORT("资源点赞"),
    COMMENT("评论"),
    SUBMIT("提交"),
    REGISTRY("注册"),
    SAVE_ARTICLE("保存文章"),
    LUCK_LOTTERY("幸运抽奖"),
    SEND_RED_PACKET("发红包"),
    OPEN_RED_PACKET("拆红包"),
    CREATE_GROUP("创建群聊"),
    ;

    private String actionName;
}
