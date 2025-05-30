-- 创建数据库 [MySQL]
create database pingme;
use pingme;

-- 用户信息主表
CREATE TABLE pingme.`chat_user`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `user_id`     char(60)        NOT NULL COMMENT '用户ID',
    `user_name`   char(30)        NOT NULL COMMENT '用户名',
    `photo`       varchar(400)    NOT NULL COMMENT '用户头像',
    `mobile`      char(20)        NOT NULL COMMENT '手机号',
    `password`    char(100)       NOT NULL COMMENT '密码',
    `signature`   varchar(500)             DEFAULT '暂无签名' COMMENT '签名',
    `status`      INT             NOT NULL DEFAULT 1 COMMENT '用户状态状态',
    `role_code`   INT             NOT NULL DEFAULT 1 COMMENT '角色',
    `create_by`   char(100)                DEFAULT NULL COMMENT '创建人',
    `create_date` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   char(100)                DEFAULT NULL COMMENT '更新人',
    `update_date` datetime                 DEFAULT NULL COMMENT '更新时间',
    `version`     int                      DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_USER_ID` (`user_id`),
    KEY `INDEX_MOBILE_PASSWORD` (`mobile`, `password`),
    KEY `INDEX_STATUS` (`status`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户信息主表';

-- 初始化默认管理员账号
INSERT INTO `pingme`.`chat_user` (`id`, `user_id`, `user_name`, `photo`, `mobile`, `password`, `status`,
                                  `role_code`,
                                  `create_by`, `create_date`, `update_by`, `update_date`, `version`)
VALUES (1, 'U_770cce9f632543588b4e8aa6ec43e6a2', '管理员',
        'https://coderutil.oss-cn-beijing.aliyuncs.com/bbs-image/file_dd489633f1bb4513a9db81be6e9d692f.png',
        'admin', '06525f4969c6cf1886ee0db86bef82df', 1, 2, 'U_770cce9f632543588b4e8aa6ec43e6a2',
        '2022-03-12 05:55:26', NULL, '2022-03-22 10:28:38', 1);

-- 好友关系表
CREATE TABLE pingme.`chat_friend`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `user_id`     char(60)        NOT NULL COMMENT '用户ID',
    `friend_id`   char(60)        NOT NULL COMMENT '好友ID',
    `status`      int(11)  DEFAULT '0' COMMENT '好友状态',
    `apply_date`  datetime DEFAULT NULL COMMENT '申请时间',
    `handle_date` datetime DEFAULT NULL COMMENT '处理时间',
    `version`     int      DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_USER_ID` (`user_id`),
    KEY `INDEX_FRIEND_ID` (`friend_id`),
    KEY `INDEX_STATUS` (`status`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT ='好友关系表';

CREATE TABLE pingme.`chat_mess`
(
    `id`           bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `sender`       char(100)       NOT NULL COMMENT '发送人',
    `receiver`     char(100)       NOT NULL COMMENT '接收人',
    `proxy_sender` char(100)                DEFAULT NULL COMMENT '消息代理发送人（应用在群聊场景）',
    `message`      text                     DEFAULT NULL COMMENT '消息内容',
    `image`        varchar(300)             DEFAULT NULL COMMENT '图片',
    `type`         tinyint(1)               DEFAULT 0 COMMENT '消息类型',
    `is_read`      tinyint(1)               DEFAULT 0 COMMENT '是否已读',
    `send_date`    datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息时间',
    `update_date`  datetime                 DEFAULT NULL COMMENT '更新时间',
    `version`      int                      DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_SENDER_PROXY_SENDER` (`sender`, `proxy_sender`),
    KEY `INDEX_RECEIVER` (`receiver`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT ='用户私信表';

-- 群组用户表
CREATE TABLE pingme.`chat_group_user`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `group_id`    char(60)        NOT NULL COMMENT '群组ID',
    `user_id`     char(60)        NOT NULL COMMENT '用户ID',
    `status`      INT             NOT NULL DEFAULT 1 COMMENT '用户状态状态',
    `create_by`   char(100)                DEFAULT NULL COMMENT '创建人',
    `create_date` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   char(100)                DEFAULT NULL COMMENT '更新人',
    `update_date` datetime                 DEFAULT NULL COMMENT '更新时间',
    `version`     int                      DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_GROUP_ID` (`group_id`),
    KEY `INDEX_USER_ID` (`user_id`),
    KEY `INDEX_STATUS` (`status`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='群组用户表';

-- 朋友圈动态表
CREATE TABLE pingme.`chat_moment`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `content`     text                     DEFAULT NULL COMMENT '正文',
    `status`      char(20)        NOT NULL COMMENT '状态',
    `images`      varchar(2000)            DEFAULT NULL COMMENT '图片',
    `video`       varchar(500)             DEFAULT NULL COMMENT '视频',
    `author`      char(100)       NOT NULL COMMENT '作者USER ID',
    `pub_date`    datetime                 DEFAULT NULL COMMENT '发布时间',
    `click_count` bigint                   DEFAULT '0' COMMENT '点击量',
    `client`      char(40)                 DEFAULT 'WEB' COMMENT '发布客户端',
    `create_by`   char(100)                DEFAULT NULL COMMENT '创建人',
    `create_date` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   char(100)                DEFAULT NULL COMMENT '更新人',
    `update_date` datetime                 DEFAULT NULL COMMENT '更新时间',
    `version`     int                      DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_AUTHOR` (`author`),
    KEY `INDEX_STATUS` (`status`),
    KEY `INDEX_PUB_DATE` (`pub_date`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='朋友圈动态表';

-- 评论表
CREATE TABLE pingme.`chat_comment`
(
    `id`            bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `resource_id`   bigint          NOT NULL COMMENT '资源ID',
    `resource_type` char(50)        NOT NULL COMMENT '资源类型',
    `author`        char(60)        NOT NULL COMMENT '作者',
    `status`        char(20)        NOT NULL COMMENT '状态',
    `content`       varchar(2000)            DEFAULT NULL COMMENT '正文',
    `images`        varchar(1000)            DEFAULT NULL COMMENT '图片',
    `reply_id`      bigint                   DEFAULT NULL COMMENT '回复ID',
    `parent_id`     bigint                   DEFAULT NULL COMMENT '父级评论ID',
    `pub_date`      datetime                 DEFAULT NULL COMMENT '发布时间',
    `create_by`     char(100)                DEFAULT NULL COMMENT '创建人',
    `create_date`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     char(100)                DEFAULT NULL COMMENT '更新人',
    `update_date`   datetime                 DEFAULT NULL COMMENT '更新时间',
    `version`       int                      DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_RESOURCE_TYPE_ID` (`resource_type`, `resource_id`),
    KEY `INDEX_REPLY_ID` (`reply_id`),
    KEY `INDEX_PARENT_ID` (`parent_id`),
    KEY `INDEX_AUTHOR` (`author`),
    KEY `INDEX_STATUS` (`status`),
    KEY `INDEX_PUB_DATE` (`pub_date`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='评论表';

-- 模板配置表
CREATE TABLE pingme.`chat_message_template`
(
    `id`       bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `code`     char(50)        NOT NULL COMMENT '模板CODE',
    `template` varchar(1000)   NOT NULL COMMENT '模板',
    `version`  int DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_CODE` (`code`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='模板配置表';

-- 资源行为记录表：点赞、收藏、分享、浏览、打赏
CREATE TABLE pingme.`chat_resource_behavior`
(
    `id`             bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `user_id`        char(60)                 DEFAULT NULL COMMENT '用户ID',
    `resource_index` int(11)         NOT NULL COMMENT '资源索引',
    `resource_type`  char(30)        NOT NULL COMMENT '资源类型',
    `behavior_type`  char(30)        NOT NULL COMMENT '行为',
    `status`         tinyint(1)               DEFAULT '0' COMMENT '状态',
    `create_by`      char(100)                DEFAULT NULL COMMENT '创建人',
    `create_date`    datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      char(100)                DEFAULT NULL COMMENT '更新人',
    `update_date`    datetime                 DEFAULT NULL COMMENT '更新时间',
    `version`        int                      DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_STATUS` (`status`),
    KEY `INDEX_USER_ID` (`user_id`),
    KEY `INDEX_RESOURCE_INDEX` (`resource_index`),
    KEY `INDEX_RESOURCE_TYPE` (`resource_type`),
    KEY `INDEX_CREATE_DATE` (`create_date`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT ='资源操作记录表';

-- 朋友圈时间线表
CREATE TABLE pingme.`chat_moment_timeline`
(
    `id`             bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `user_id`        char(60)                 DEFAULT NULL COMMENT '用户ID',
    `moment_user_id` char(60)                 DEFAULT NULL COMMENT '动态作者ID',
    `moment_id`      bigint          NOT NULL COMMENT '动态ID',
    `deleted`        tinyint(1)               DEFAULT '0' COMMENT '是否被删除',
    `time`           datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `version`        int                      DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_DELETED` (`deleted`),
    KEY `INDEX_USER_ID` (`user_id`),
    KEY `INDEX_MOMENT_USER_ID` (`moment_user_id`),
    KEY `INDEX_MOMENT_ID` (`moment_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT ='朋友圈时间线';

-- 用户消息表 coder_util_message
CREATE TABLE pingme.`chat_moment_message`
(
    `id`            bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `category`      char(50)        NOT NULL COMMENT '消息分类',
    `type`          char(50)        NOT NULL COMMENT '消息类型',
    `resource_type` char(50)                 DEFAULT NULL COMMENT '资源类型',
    `resource_id`   char(100)                DEFAULT NULL COMMENT '资源ID',
    `content`       varchar(1000)            DEFAULT NULL COMMENT '消息正文',
    `activity_name` varchar(100)             DEFAULT NULL COMMENT '活动名',
    `activity_url`  varchar(300)             DEFAULT NULL COMMENT '活动链接',
    `from_user_id`  char(100)       NOT NULL COMMENT '消息发送人',
    `to_user_id`    char(100)       NOT NULL COMMENT '消息接受人',
    `state`         tinyint(1)               DEFAULT 0 COMMENT '消息状态',
    `create_date`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `read_date`     datetime                 DEFAULT NULL COMMENT '读取时间',
    `version`       int                      DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_TO_USER_ID_CATEGORY` (`to_user_id`, `category`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='朋友圈消息表';

-- 红包信息表
CREATE TABLE pingme.`chat_red_packet`
(
    `id`               bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `send_user_id`     char(100)       NOT NULL COMMENT '红包发送人',
    `receiver_user_id` char(100)       NOT NULL COMMENT '接受人',
    `type`             int(4)          NOT NULL COMMENT '消息类型',
    `count`            int(4)          NOT NULL COMMENT '红包个数',
    `status`           int(4)          NOT NULL COMMENT '状态',
    `total_money`      DECIMAL(10, 2)           default '0.00' COMMENT '金额',
    `create_by`        char(100)                DEFAULT NULL COMMENT '创建人',
    `create_date`      datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `expire_date`      datetime        NOT NULL COMMENT '过期时间',
    `update_by`        char(100)                DEFAULT NULL COMMENT '更新人',
    `update_date`      datetime                 DEFAULT NULL COMMENT '更新时间',
    `version`          int                      DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_SEND_USER_ID` (`send_user_id`),
    KEY `INDEX_STATUS_EXPIRE_DATE` (`status`, `expire_date`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT ='红包信息表';

-- 红包拆分记录明细表
CREATE TABLE pingme.`chat_red_packet_record`
(
    `id`            bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `red_packet_id` bigint          NOT NULL COMMENT '红包id',
    `user_id`       char(100)       NOT NULL COMMENT '领取人',
    `money`         DECIMAL(10, 2)           default '0.00' COMMENT '领取金额',
    `create_date`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `INDEX_RED_PACKET_ID` (`red_packet_id`),
    KEY `INDEX_USER_ID` (`user_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT ='红包拆分记录明细表';

-- 用户钱包
CREATE TABLE pingme.`chat_user_wallet`
(
    `id`             bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `trans_event`    int(4)          NOT NULL COMMENT '事件类型',
    `trans_type`     int(4)          NOT NULL COMMENT '收入/支出',
    `user_id`        char(100)       NOT NULL COMMENT '用户id',
    `target_user_id` char(100)       NOT NULL COMMENT '目标用户',
    `money`          DECIMAL(10, 2)           default '0.00' COMMENT '流转金额',
    `trans_date`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '流转时间',
    PRIMARY KEY (`id`),
    KEY `INDEX_USER_ID` (`user_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT ='用户钱包';

-- 公众号文章表
CREATE TABLE pingme.chat_article
(
    `id`             bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `public_account` char(60)        NOT NULL COMMENT '绑定公众号账号',
    `author`         char(60)        NOT NULL COMMENT '作者',
    `title`          varchar(100)    NOT NULL COMMENT '文章标题',
    `content`        longtext        NOT NULL COMMENT '正文',
    `cover`          varchar(400)    NOT NULL COMMENT '封面图',
    `redirect_url`   varchar(400)             DEFAULT NULL COMMENT '外部连接',
    `description`    varchar(200)             DEFAULT NULL COMMENT '摘要/智能总结',
    `signs`          varchar(200)             DEFAULT NULL COMMENT '标签',
    `plan_push_date` datetime                 DEFAULT NULL COMMENT '计划推文时间',
    `status`         tinyint(1)               DEFAULT 1 COMMENT '文章状态',
    `create_by`      char(100)                DEFAULT NULL COMMENT '创建人',
    `create_date`    datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      char(100)                DEFAULT NULL COMMENT '更新人',
    `update_date`    datetime                 DEFAULT NULL COMMENT '更新时间',
    `version`        int                      DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_STATUS_PUBLIC_ACCOUNT` (`status`, `public_account`),
    KEY `INDEX_PLAN_PUSH_DATE` (`plan_push_date`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='公众号文章表';

-- 滑块验证配置信息
CREATE TABLE pingme.`chat_slide_verification`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `image`       char(200)                DEFAULT NULL COMMENT '主图',
    `x`           int(11)                  DEFAULT 0 COMMENT 'x坐标',
    `y`           int(11)                  DEFAULT 0 COMMENT 'y坐标',
    `count`       int(11)                  DEFAULT 0 COMMENT '浏览量',
    `status`      tinyint(1)               DEFAULT '0' COMMENT '状态',
    `create_by`   char(100)                DEFAULT NULL COMMENT '创建人',
    `create_date` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   char(100)                DEFAULT NULL COMMENT '更新人',
    `update_date` datetime                 DEFAULT NULL COMMENT '更新时间',
    `version`     int                      DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_STATUS` (`status`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT ='滑块验证配置信息';

-- 初始化验证码配置，管理后台可上传
INSERT INTO `pingme`.`chat_slide_verification`(`image`, `x`, `y`, `count`, `status`, `create_by`, `create_date`,
                                               `update_by`, `update_date`, `version`)
VALUES ('/image/slide/file_5ed7dbb98fb04fa490d157e8798d1f23.jpeg', 0, 0, 0, 1, NULL, '2024-09-19 01:16:41', NULL, NULL,
        1);
INSERT INTO `pingme`.`chat_slide_verification`(`image`, `x`, `y`, `count`, `status`, `create_by`, `create_date`,
                                               `update_by`, `update_date`, `version`)
VALUES ('/image/slide/file_5b9df5f2de52439dbc74c86a6f9e749d.jpeg', 0, 0, 0, 1, NULL, '2024-09-19 01:16:46', NULL, NULL,
        1);
INSERT INTO `pingme`.`chat_slide_verification`(`image`, `x`, `y`, `count`, `status`, `create_by`, `create_date`,
                                               `update_by`, `update_date`, `version`)
VALUES ('/image/slide/file_cc3fc5dea48a43c2923f9e001b9f1b85.jpeg', 0, 0, 0, 1, NULL, '2024-09-19 01:16:51', NULL, NULL,
        1);
INSERT INTO `pingme`.`chat_slide_verification`(`image`, `x`, `y`, `count`, `status`, `create_by`, `create_date`,
                                               `update_by`, `update_date`, `version`)
VALUES ('/image/slide/file_f36ec169377842dd8ec0c65a43619811.jpeg', 0, 0, 0, 1, NULL, '2024-09-19 01:16:56', NULL, NULL,
        1);
INSERT INTO `pingme`.`chat_slide_verification`(`image`, `x`, `y`, `count`, `status`, `create_by`, `create_date`,
                                               `update_by`, `update_date`, `version`)
VALUES ('/image/slide/file_b44c3489577b4dfcaae9c09220ff677c.jpeg', 0, 0, 0, 1, NULL, '2024-09-19 01:17:00', NULL, NULL,
        1);

-- 抽奖活动配置表
CREATE TABLE pingme.`chat_lottery_activity`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `activity_id` char(100)       NOT NULL COMMENT '抽奖活动业务ID',
    `name`        varchar(100)    NOT NULL COMMENT '活动名称',
    `description` varchar(500)    NOT NULL COMMENT '活动描述',
    `integral`    int(11)         NOT NULL DEFAULT 0 COMMENT '每次抽奖需要消耗的金币个数',
    `cover`       varchar(300)    NOT NULL COMMENT '活动封面图',
    `money`       DECIMAL(10, 2)           default '0.00' COMMENT '每次抽奖消耗钱数',
    `status`      int(11)         NOT NULL COMMENT '活动状态',
    `create_by`   char(100)                DEFAULT NULL COMMENT '创建人',
    `create_date` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   char(100)                DEFAULT NULL COMMENT '更新人',
    `update_date` datetime                 DEFAULT NULL COMMENT '更新时间',
    `version`     int                      DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_ACTIVITY_ID_STATUS` (`activity_id`, `status`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='抽奖活动配置表';

-- 抽奖活动奖品表
CREATE TABLE pingme.`chat_lottery_item`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `activity_id` char(100)       NOT NULL COMMENT '抽奖活动业务ID',
    `type`        int(11)         NOT NULL COMMENT '奖品类型',
    `name`        varchar(100)    NOT NULL COMMENT '奖品名称',
    `slot`        int(11)         NOT NULL DEFAULT 1 COMMENT '奖品槽位1～8',
    `icon`        varchar(300)    NOT NULL COMMENT '奖品ICON',
    `cover`       varchar(300)    NOT NULL COMMENT '奖品列表显示封面图',
    `stock`       int(11)         NOT NULL COMMENT '奖品库存',
    `create_by`   char(100)                DEFAULT NULL COMMENT '创建人',
    `create_date` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   char(100)                DEFAULT NULL COMMENT '更新人',
    `update_date` datetime                 DEFAULT NULL COMMENT '更新时间',
    `version`     int                      DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_ACTIVITY_ID_SLOT_STOCK` (`activity_id`, `slot`, `stock`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='抽奖活动奖品表';

-- 抽奖结果订单表
CREATE TABLE pingme.`chat_lottery_order`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `order_id`    char(100)       NOT NULL COMMENT '订单号',
    `activity_id` char(100)       NOT NULL COMMENT '抽奖活动业务ID',
    `item_id`     bigint          NOT NULL COMMENT '中奖奖品ID',
    `user_id`     char(100)                DEFAULT NULL COMMENT '中奖用户ID',
    `status`      int(11)         NOT NULL COMMENT '订单状态',
    `create_by`   char(100)                DEFAULT NULL COMMENT '创建人',
    `create_date` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   char(100)                DEFAULT NULL COMMENT '更新人',
    `update_date` datetime                 DEFAULT NULL COMMENT '更新时间',
    `version`     int                      DEFAULT '0' COMMENT '版本',
    PRIMARY KEY (`id`),
    KEY `INDEX_ORDER_ID` (`order_id`),
    KEY `INDEX_USER_ID` (`user_id`),
    KEY `INDEX_ITEM_ID` (`item_id`),
    KEY `INDEX_ACTIVITY_ID_STATUS` (`activity_id`, `status`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='抽奖结果订单表';