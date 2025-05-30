package com.lb.pingme.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "chat_comment")
public class CommentEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * 资源ID
     */
    @Column(name = "resource_id")
    private Long resourceId;

    /**
     * 资源类型
     */
    @Column(name = "resource_type")
    private String resourceType;

    /**
     * 评论正文
     */
    @Column(name = "content")
    private String content;

    /**
     * 评论图片
     */
    @Column(name = "images")
    private String images;

    /**
     * 回复ID
     */
    @Column(name = "reply_id")
    private Long replyId;

    /**
     * 父级评论ID
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 作者
     */
    @Column(name = "author")
    private String author;

    /**
     * 状态
     */
    @Column(name = "status")
    private String status;

    /**
     * 发布时间
     */
    @Column(name = "pub_date")
    private Date pubDate;
}
