package com.lb.pingme.repository.dao;

import com.lb.pingme.repository.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICommentDAO extends JpaSpecificationExecutor<CommentEntity>, JpaRepository<CommentEntity, Long> {


    CommentEntity findAllById(Long id);

    @Query(value = "select * from web_chat_comment c where c.parent_id = ?1 and c.status = ?2 order by id asc"
            + " limit ?3", nativeQuery = true)
    List<CommentEntity> findAllByParentIdAndStatus(Long parentId, String status, Integer size);

    /***
     * 查询资源评论数量，这里指主楼数量
     * @param resourceId
     * @param resourceType
     * @param status
     * @return
     */
    Long countByResourceIdAndResourceTypeAndStatusAndParentIdIsNull(Long resourceId, String resourceType, String status);

    /***
     * 查询外显评论
     * @param resourceType
     * @param resourceId
     * @param status
     * @return
     */
    @Query(value = "select * from web_chat_comment c where c.resource_type = ?1 and c.resource_id = ?2 and c.status = ?3 order by id desc limit ?4",
            nativeQuery = true)
    List<CommentEntity> getOutSideCommentListLimit(String resourceType, Long resourceId, String status, Integer limit);
}
