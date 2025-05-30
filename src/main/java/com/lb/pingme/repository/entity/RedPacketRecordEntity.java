package com.lb.pingme.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "chat_red_packet_record")
public class RedPacketRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * 红包id
     */
    @Column(name = "red_packet_id")
    private Long redPacketId;

    /**
     * 领取人
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 总金额
     */
    @Column(name = "money")
    private BigDecimal money;

    /**
     * 过期时间
     */
    @Column(name = "create_date")
    private Date createDate;
}
