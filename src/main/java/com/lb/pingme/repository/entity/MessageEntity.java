package com.lb.pingme.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "chat_moment_message")
public class MessageEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * 消息分类
     */
    @Column(name = "category")
    private String category;

    /**
     * 消息类型
     */
    @Column(name = "type")
    private String type;

    /**
     * 资源类型
     */
    @Column(name = "resource_type")
    private String resourceType;

    /**
     * 资源id
     */
    @Column(name = "resource_id")
    private String resourceId;

    /***
     * 消息正文
     */
    @Column(name = "content")
    private String content;

    /**
     * 活动名称
     */
    @Column(name = "activity_name")
    private String activityName;

    /**
     * 活动链接
     */
    @Column(name = "activity_url")
    private String activityUrl;

    /**
     * 消息发送人
     */
    @Column(name = "from_user_id")
    private String fromUserId;

    /**
     * 消息接收人
     */
    @Column(name = "to_user_id")
    private String toUserId;

    /**
     * 是否读取
     */
    @Column(name = "state")
    private Boolean state;

    /**
     * 消息发送时间
     */
    @Column(name = "create_date")
    private Date createDate;

    /**
     * 消息读取时间
     */
    @Column(name = "read_date")
    private Date readDate;

    @Version
    @Column(name = "version")
    private Integer version;

    @PrePersist
    public void prePersist() {
        Date now = new Date();
        if (this.createDate == null) {
            this.createDate = now;
        }
    }
}
