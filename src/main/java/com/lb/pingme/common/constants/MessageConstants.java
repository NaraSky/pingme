package com.lb.pingme.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 消息系统常量类
 * 包含消息来源、分类和类型的定义及转换方法
 */
public class MessageConstants {

    /**
     * 系统发送的消息，系统用户标识
     * 用于表示系统自动发送的消息来源
     */
    public static final String FROM_SYSTEM = "SYSTEM";

    /**
     * 消息分类枚举
     * 定义不同类型的消息类别及其显示名称
     */
    @Getter
    @AllArgsConstructor
    public enum CategoryEnum {

        SYSTEM("系统消息"),
        COMMENT_REPLY("评论与回复"),
        LIKE_COLLECT("点赞与收藏"),
        FOCUS("粉丝关注");

        private String categoryName;
        
        /**
         * 获取枚举名称（用于匹配）
         * @return 枚举常量的名称
         */
        public String getCategory() {
            return this.name();
        }
    }

    /**
     * 根据消息分类代码获取对应的显示名称
     * @param category 消息分类代码
     * @return 对应的显示名称，未找到则返回空字符串
     */
    public static String getCategoryName(String category) {
        if (StringUtils.isBlank(category)) {
            return "";
        }
        for (CategoryEnum categoryEnum : CategoryEnum.values()) {
            if (categoryEnum.getCategory().equals(category)) {
                return categoryEnum.getCategoryName();
            }
        }
        return "";
    }

    /**
     * 消息类型枚举
     * 定义具体的消息类型及其显示名称
     */
    @Getter
    @AllArgsConstructor
    public enum TypeEnum {

        LIKE("点赞"),
        LIKE_COMMENT("点赞"),
        COLLECT("收藏"),
        COMMENT("评论消息"),
        REPLY("回复消息"),
        OTHER("其他");

        private String typeName;

        /**
         * 获取枚举名称（用于匹配）
         * @return 枚举常量的名称
         */
        public String getType() {
            return this.name();
        }
    }

    /**
     * 根据消息类型代码获取对应的显示名称
     * @param type 消息类型代码
     * @return 对应的显示名称，未找到则返回空字符串
     */
    public static String getTypeName(String type) {
        if (StringUtils.isBlank(type)) {
            return "";
        }
        for (TypeEnum typeEnum : TypeEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum.getTypeName();
            }
        }
        return "";
    }
}