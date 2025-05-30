package com.lb.pingme.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "chat_moment_timeline")
public class MomentTimeLineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "userId")
    private String userId;

    @Column(name = "moment_user_id")
    private String momentUserId;

    @Column(name = "moment_id")
    private Long momentId;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "time")
    private Date time;

    @Version
    @Column(name = "version")
    private Integer version;
}
