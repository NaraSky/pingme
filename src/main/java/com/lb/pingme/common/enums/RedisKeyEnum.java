package com.lb.pingme.common.enums;

import com.lb.pingme.common.constants.WebConstant;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum RedisKeyEnum {

    /***
     * 注册限流，5S内一次
     */
    USER_REGISTRY_LIMIT("USER_REGISTRY_LIMIT", 5L),

    /**
     * 创建群聊限流，10S内一次
     */
    CREATE_GROUP_LIMIT("CREATE_GROUP_LIMIT", 10L),

    /**
     * 创建机器人防重复触发，10S内一次
     */
    CREATE_ROBOT_LIMIT("CREATE_ROBOT_LIMIT", 10L),

    /**
     * 创建公众号防重复触发，10S内一次
     */
    CREATE_PUBLIC_ACCOUNT_LIMIT("CREATE_PUBLIC_ACCOUNT_LIMIT", 10L),

    /**
     * 公众号列表缓存
     */
    PUBLIC_ACCOUNT_LIST_CACHE("PUBLIC_ACCOUNT_LIST_CACHE", 24 * 60 * 60L),

    /**
     * 用户信息缓存 缓存7天
     */
    USER_INFO_CACHE("USER_INFO_CACHE", 7 * 24 * 60 * 60L),

    /***
     * 私信用户列表
     */
    MESS_USER_LIST_KEY("MESS_USER_LIST_KEY", -1L),

    /***
     * 用户私信消息
     */
    USER_CHAT_MESS_CACHE_KEY("USER_CHAT_MESS_CACHE_KEY", 7 * 24 * 60 * 60L),

    /**
     * 用户消息生产 消息队列
     */
    QUEUE_WEB_USER_MESSAGE("QUEUE_WEB_USER_MESSAGE", -1L),

    QUEUE_WEB_OPEN_RED_PACKET_MESSAGE("QUEUE_WEB_OPEN_RED_PACKET_MESSAGE", -1L),

    /**
     * 文章发布消息队列
     */
    QUEUE_ARTICLE_PUBLISH_MESSAGE("QUEUE_ARTICLE_PUBLISH_MESSAGE", -1L),

    /**
     * 文章延迟发布消息队列
     */
    QUEUE_ARTICLE_DELAY_PUBLISH_MESSAGE("QUEUE_ARTICLE_DELAY_PUBLISH_MESSAGE", -1L),

    /**
     * 消息模板
     */
    MESSAGE_CONTENT_TEMPLATE_CACHE("MESSAGE_CONTENT_TEMPLATE_CACHE", -1L),

    /**
     * 消息详情缓存
     */
    MESSAGE_DETAIL_CACHE("MESSAGE_DETAIL_CACHE", 30 * 24 * 60 * 60L),

    /**
     * 新消息数缓存
     */
    MESSAGE_UNREAD_COUNT_CACHE("MESSAGE_UNREAD_COUNT_CACHE", -1L),

    /**
     * 消息数量缓存
     */
    MESSAGE_USER_UN_READ_COUNT_CACHE("MESSAGE_USER_UN_READ_COUNT_CACHE", -1L),

    /**
     * 消息列表缓存
     */
    MESSAGE_LIST_CACHE("MESSAGE_LIST_CACHE", -1L),

    /***
     * 私信消息缓存
     */
    MESS_DETAIL_CACHE_KEY("MESS_DETAIL_CACHE_KEY", -1L),

    /**
     * 未读私信数量
     */
    UN_READ_MESS_COUNT_CACHE("UN_READ_MESS_COUNT_CACHE", -1L),

    /**
     * 未读私信人数量
     */
    UN_READ_MESS_USER_SET_CACHE("UN_READ_MESS_USER_SET_CACHE", -1L),

    /***
     * 用户登录Session PREFIX
     */
    USER_SESSION_PREFIX("USER_SESSION_PREFIX", 3 * 24 * 60 * 60L),

    /**
     * 群组下成员用户id列表缓存
     */
    GROUP_USER_ID_LIST_CACHE("GROUP_USER_ID_LIST_CACHE", 7 * 24 * 60 * 60L),

    /**
     * 用户加入的所有群组
     */
    USER_ATTEND_GROUP_ID_LIST_CACHE("USER_ATTEND_GROUP_ID_LIST_CACHE", 7 * 24 * 60 * 60L),

    /**
     * 资源操作的用户列表
     */
    RESOURCE_BEHAVIOR_USERS_CACHE("RESOURCE_BEHAVIOR_USERS_CACHE", -1L),

    /**
     * 资源行为计数缓存
     */
    RESOURCE_BEHAVIOR_COUNT_CACHE("RESOURCE_BEHAVIOR_COUNT_CACHE", -1L),

    /***
     * 用户点赞的资源列表
     */
    USER_BEHAVIOR_RESOURCE_CACHE("USER_BEHAVIOR_RESOURCE_CACHE", -1L),

    /**
     * 社区内容详情缓存
     */
    MOMENT_DETAIL_CACHE("MOMENT_DETAIL_CACHE", 7 * 25 * 60* 60L),

    /**
     * 朋友圈动态列表
     */
    MOMENT_TIME_LINE_CACHE("MOMENT_TIME_LINE_CACHE", -1L),

    /**
     * 外显评论
     */
    COMMENT_OUT_LIST_CACHE("COMMENT_OUT_LIST_CACHE", -1L),

    /**
     * 评论数缓存
     */
    COMMENT_COUNT_CACHE("COMMENT_COUNT_CACHE", -1L),

    /**
     * 红包详情
     */
    RED_PACKET_DETAIL_CACHE("RED_PACKET_DETAIL_CACHE", 2 * 24 * 60 * 60L),

    /**
     * 用户钱包余额
     */
    USER_WALLET_BALANCE_CACHE("USER_WALLET_BALANCE_CACHE", 3* 24 * 60 * 60L),

    /**
     * 用户钱包余额刷新 加锁
     */
    REFRESH_USER_WALLET_BALANCE_CACHE_LOCK("USER_WALLET_BALANCE_CACHE_LOCK", -1L),

    /**
     * 红包累计被抢次数
     */
    RED_PACKET_GRAD_COUNT("RED_PACKET_GRAD_COUNT", -1L),

    /**
     * 红包剩余可拆分金额
     */
    RED_PACKET_BALANCE_COUNT("RED_PACKET_BALANCE_COUNT", -1L),

    /**
     * 红包领取的用户记录
     */
    RED_PACKET_RECEIVER_COUNT("RED_PACKET_RECEIVER_COUNT", -1L),

    /**
     * 红包拆分记录
     */
    RED_PACKET_RECEIVER_TIMELINE_COUNT("RED_PACKET_RECEIVER_TIMELINE_COUNT", -1L),

    /**
     * 公众号单篇文章详情缓存, 3天有效
     */
    ARTICLE_DETAIL_CACHE("ARTICLE_DETAIL_CACHE", 3 * 24 * 60 * 60L),

    /**
     * ES 数据增量同步，最近一次成功同步时间缓存
     */
    ES_SYNC_LAST_TIME("ES_SYNC_LAST_TIME", -1L),

    /**
     * 滑块验证码
     */
    SLIDE_VERIFICATION_CACHE("SLIDE_VERIFICATION_CACHE", 30 * 25 * 60* 60L),
    SLIDE_VERIFICATION_TOKEN_CACHE("SLIDE_VERIFICATION_TOKEN_CACHE", 60L),
    SLIDE_VERIFICATION_TOKEN_VALIDATE_RESULT_CACHE("SLIDE_VERIFICATION_TOKEN_VALIDATE_RESULT_CACHE", 60L),

    /**
     * 抽奖活动缓存
     */
    LOTTERY_ACTIVITY_DETAIL_CACHE("LOTTERY_ACTIVITY_DETAIL_CACHE", 30 * 24 * 60 * 60L),

    /**
     * 历史抽奖活动缓存
     */
    LOTTERY_ACTIVITY_HISTORY_CACHE("LOTTERY_ACTIVITY_HISTORY_CACHE", -1L),

    /**
     * 最近一期活动ID缓存
     */
    LOTTERY_ACTIVITY_LAST_ID_CACHE("LOTTERY_ACTIVITY_LAST_ID_CACHE", -1L),

    /**
     * 抽奖活动奖品余量缓存
     */
    LOTTERY_ACTIVITY_ITEM_STOCK_CACHE("LOTTERY_ACTIVITY_ITEM_STOCK_CACHE", -1L),

    /**
     * 抽奖商品详情缓存
     */
    LOTTERY_ACTIVITY_ITEM_DETAIL_CACHE("LOTTERY_ACTIVITY_ITEM_DETAIL_CACHE", 30 * 24 * 60 * 60L),

    /**
     * 中奖结果缓存
     */
    LOTTERY_ACTIVITY_ORDER_CACHE("LOTTERY_ACTIVITY_ORDER_CACHE", 30 * 24 * 60 * 60L),

    /**
     * 订单详情缓存
     */
    LOTTERY_ACTIVITY_ORDER_DETAIL_CACHE("LOTTERY_ACTIVITY_ORDER_DETAIL_CACHE", 30 * 24 * 60 * 60L),

    /**
     * 用户中奖结果缓存
     */
    LOTTERY_ACTIVITY_USER_ORDER_CACHE("LOTTERY_ACTIVITY_USER_ORDER_CACHE", 30 * 24 * 60 * 60L),

    /**
     * 抽奖结果订单
     */
    QUEUE_LOTTERY_ORDER_MESSAGE("QUEUE_LOTTERY_ORDER_MESSAGE", -1L),


    ;


    RedisKeyEnum(String key, Long expireTime) {
        this.key = key;
        this.expireTime = expireTime;
    }

    private String key;

    /**
     * 有效时间,单位秒
     */
    private long expireTime;

    public String getKey(String... suffix) {
        StringBuilder tmpSuffix = new StringBuilder();
        if (suffix != null && suffix.length > 0) {
            for (String str : suffix) {
                if (StringUtils.isNotBlank(str)) {
                    tmpSuffix.append("_").append(str);
                }
            }
        }
        return WebConstant.REDIS_KEY_PREFIX + this.name() + tmpSuffix;
    }
}
