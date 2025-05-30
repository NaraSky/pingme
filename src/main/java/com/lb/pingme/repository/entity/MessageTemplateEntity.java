package com.lb.pingme.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chat_message_template")
public class MessageTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /***
     * 模板配置
     */
    @Column(name = "CODE")
    private String code;

    /***
     * 模板配置
     */
    @Column(name = "template")
    private String template;

    @Version
    @Column(name = "version")
    private Integer version;
}
