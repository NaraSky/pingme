package com.lb.pingme.repository.dao;

import com.lb.pingme.repository.entity.MomentTimeLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMomentTimeLineDAO extends JpaSpecificationExecutor<MomentTimeLineEntity>, JpaRepository<MomentTimeLineEntity, Long> {

    List<MomentTimeLineEntity> findAllByUserIdAndDeletedOrderByIdDesc(String userId, Boolean deleted);

    List<MomentTimeLineEntity> findAllByUserIdAndMomentUserIdAndDeletedOrderByIdDesc(String userId, String momentUserId, Boolean deleted);

    List<MomentTimeLineEntity> findAllByMomentIdAndDeletedOrderByIdDesc(Long momentId, Boolean deleted);
}
