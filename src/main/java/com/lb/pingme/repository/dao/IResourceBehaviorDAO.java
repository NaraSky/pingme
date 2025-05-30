package com.lb.pingme.repository.dao;

import com.lb.pingme.repository.entity.ResourceBehaviorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface IResourceBehaviorDAO extends JpaSpecificationExecutor<ResourceBehaviorEntity>, JpaRepository<ResourceBehaviorEntity, Long> {

    /***
     * 用户 - 根据资源索引ID、资源类型、行为状态查询用户操作记录
     * @param userId
     * @param resourceIndex
     * @param resourceType
     * @param behaviorType
     * @return
     */
    List<ResourceBehaviorEntity> findAllByUserIdAndResourceIndexAndResourceTypeAndBehaviorType(
            String userId, Long resourceIndex, String resourceType, String behaviorType);

    /***
     * 用户 - 根据资源索引ID、资源类型、行为状态查询用户操作记录
     * @param userId
     * @param resourceIndex
     * @param resourceType
     * @param behaviorType
     * @param status
     * @return
     */
    List<ResourceBehaviorEntity> findAllByUserIdAndResourceIndexAndResourceTypeAndBehaviorTypeAndStatus(
            String userId, Long resourceIndex, String resourceType, String behaviorType, Boolean status);

    /***
     * 用户 - 查询用户操作的某类型资源总数
     * @param userId
     * @param resourceType
     * @param status
     * @return
     */
    Long countAllByUserIdAndResourceIndexAndResourceTypeAndBehaviorTypeAndStatus(
            String userId, Long resourceIndex, String resourceType, String behaviorType, Boolean status);

    /***
     * 资源 - 根据资源索引ID、资源类型、行为状态查询资源被操作的记录
     * @param resourceIndex
     * @param resourceType
     * @param status
     * @return
     */
    List<ResourceBehaviorEntity> findAllByResourceIndexAndResourceTypeAndBehaviorTypeAndStatus(
            Long resourceIndex, String resourceType, String behaviorType, Boolean status);

    /***
     * 资源 - 根据用户ID、资源类型、行为状态查询资源被操作的记录
     * @param userId
     * @param resourceType
     * @param status
     * @return
     */
    List<ResourceBehaviorEntity> findAllByUserIdAndResourceTypeAndBehaviorTypeAndStatus(
            String userId, String resourceType, String behaviorType, Boolean status);

    /***
     * 资源 - 查询某个资源的操作总数
     * @param resourceIndex
     * @param resourceType
     * @param status
     * @return
     */
    Long countAllByResourceIndexAndResourceTypeAndBehaviorTypeAndStatus(
            Long resourceIndex, String resourceType, String behaviorType, Boolean status);

    @Query(value = "select b.RESOURCE_INDEX as rid, count(1) as ctn from web_chat_resource_behavior b " +
            "where b.RESOURCE_INDEX in (?1) and b.STATUS = 1 and b.RESOURCE_TYPE = ?2 and b.BEHAVIOR_TYPE = ?3 group by b.RESOURCE_INDEX", nativeQuery = true)
    List<Map<String, Object>> countBehaviorByResourceList(List<Long> resourceIndexList, String resourceType, String behaviorType);

    Long countAllByBehaviorTypeAndResourceTypeAndResourceIndexAndStatus(String beType, String reType, Long reIndex,
                                                                        Boolean status);

    List<ResourceBehaviorEntity> findAllByUserIdAndStatusAndResourceIndexInAndResourceTypeAndBehaviorType(String userId, Boolean status, List<Long> resourceIndexList, String resourceType, String behaviorType);

    Page<ResourceBehaviorEntity> findAllByUserIdAndBehaviorTypeAndResourceTypeAndStatus(
            String userId, String behaviorType, String resourceType, Boolean status, Pageable pageable);

    Long countByUserIdAndResourceType(String userId, String resourceType);

    @Query(value = "select resource_index as id, count(1) as cnt from web_chat_resource_behavior where " +
            "resource_type = :resType and status = 1 and BEHAVIOR_TYPE = :behType" +
            " group by resource_index",
            nativeQuery = true)
    List<Map<String, Long>> getResourceBehaviorCount(String resType, String behType);
}
