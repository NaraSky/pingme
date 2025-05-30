package com.lb.pingme.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "chat_user_wallet")
public class UserWalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * 事件类型
     */
    @Column(name = "trans_event")
    private Integer transEvent;

    /**
     * 收入 / 支出
     */
    @Column(name = "trans_type")
    private Integer transType;

    /**
     * 当前用户
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 目前用户
     */
    @Column(name = "target_user_id")
    private String targetUserId;

    /**
     * 总金额
     */
    @Column(name = "money")
    private BigDecimal money;

    /**
     * 流转时间
     */
    @Column(name = "trans_date")
    private Date transDate;
}
