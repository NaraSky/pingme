package com.lb.pingme.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "chat_friend")
public class FriendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "friend_id")
    private String friendId;

    /**
     * 申请时间
     */
    @Column(name = "apply_date")
    private Date applyDate;

    @Column(name = "handle_date")
    private Date handleDate;

    @Column(name = "status")
    private Integer status;

    @Version
    @Column(name = "version")
    private Integer version;
}
