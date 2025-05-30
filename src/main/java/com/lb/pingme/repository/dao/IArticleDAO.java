package com.lb.pingme.repository.dao;

import com.lb.pingme.repository.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

// 文章数据访问接口，继承JPA的相关接口以获得基础的CRUD功能
@Repository
public interface IArticleDAO extends JpaSpecificationExecutor<ArticleEntity>,
        JpaRepository<ArticleEntity, Long> {

    // 更新文章推送状态为已推送（状态码2）
    // 使用@Transactional确保事务完整性
    @Transactional
    @Modifying
    @Query(value = "update ArticleEntity a set a.status = 2 where a.id = :id")
    int updatePushedStatus(Long id);

}