package com.lb.pingme.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "chat_red_packet")
public class RedPacketEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * 红包发送人
     */
    @Column(name = "send_user_id")
    private String sendUserId;

    /**
     * 可能是人 / 群
     */
    @Column(name = "receiver_user_id")
    private String receiverUserId;

    /**
     * 拼手气、普通红包（平均分配）
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 总金额
     */
    @Column(name = "total_money")
    private BigDecimal totalMoney;

    /**
     * 一共几个，发给个人只能一次拆一个，发给群聊可以配置多个
     */
    @Column(name = "count")
    private Integer count;

    /**
     * 红包状态：新建、过期、结束（提前抽完）
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 过期时间
     */
    @Column(name = "expire_date")
    private Date expireDate;
}
