package com.lb.pingme.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "chat_article")
public class ArticleEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * 绑定公众号
     */
    @Column(name = "public_account")
    private String publicAccount;

    @Column(name = "author")
    private String author;

    @Column(name = "title")
    private String title;

    /**
     * 文章封面主图
     */
    @Column(name = "cover")
    private String cover;

    /**
     * 文章总结
     */
    @Column(name = "description")
    private String description;

    /**
     * 文章正文
     */
    @Column(name = "content")
    private String content;

    /**
     * 文章标签
     */
    @Column(name = "signs")
    private String signs;

    /**
     * 计划推送时间
     */
    @Column(name = "plan_push_date")
    private Date planPushDate;

    /**
     * 文章状态
     *
     * @see com.lb.pingme.common.enums.ArticleStatusEnum
     */
    @Column(name = "status")
    private Integer status;
}
