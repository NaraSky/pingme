package com.lb.pingme.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chat_group_user")
public class GroupUserEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * 群组ID
     */
    @Column(name = "group_id")
    private String groupId;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 用户状态状态
     */
    @Column(name = "status")
    private Integer status;

}
