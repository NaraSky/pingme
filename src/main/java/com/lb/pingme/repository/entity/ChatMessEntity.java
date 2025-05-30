package com.lb.pingme.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "chat_mess")
public class ChatMessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "sender")
    private String sender;

    /***
     * 代理发送人（群聊真实消息发送人）点对点消息，proxySender为空
     */
    @Column(name = "proxy_sender")
    private String proxySender;

    @Column(name = "receiver")
    private String receiver;

    /**
     * 消息类型
     *
     * @see com.lb.pingme.common.enums.ChatMessageTypeEnum
     */
    @Column(name = "type")
    private Integer type;

    /***
     * 正文
     */
    @Column(name = "message")
    private String message;

    /***
     * 状态
     */
    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "send_date")
    private Date sendDate;

    @Column(name = "update_date")
    private Date updateDate;

    @Version
    @Column(name = "version")
    private Integer version;

    @PrePersist
    public void prePersist() {
        Date now = new Date();
        if (this.sendDate == null) {
            this.sendDate = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updateDate = new Date();
    }
}
