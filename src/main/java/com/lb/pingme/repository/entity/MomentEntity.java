package com.lb.pingme.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "chat_moment")
public class MomentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "images")
    private String images;

    @Column(name = "video")
    private String video;

    @Column(name = "status")
    private String status;

    @Column(name = "author")
    private String author;

    @Column(name = "pub_date")
    private Date pubDate;

    @Column(name = "click_count")
    private Long clickCount;

    @Column(name = "client")
    private String client;
}
