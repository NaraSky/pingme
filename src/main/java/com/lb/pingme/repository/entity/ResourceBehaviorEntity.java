package com.lb.pingme.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chat_resource_behavior")
public class ResourceBehaviorEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "resource_index")
    private Long resourceIndex;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "behavior_type")
    private String behaviorType;

    @Column(name = "status")
    private Boolean status;
}
