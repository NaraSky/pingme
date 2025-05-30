package com.lb.pingme.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chat_slide_verification")
public class SlideVerificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "image")
    private String image;

    @Column(name = "status")
    private Integer status;

    @Version
    @Column(name = "version")
    private Integer version;
}
