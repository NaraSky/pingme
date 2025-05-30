package com.lb.pingme.repository.dao;

import com.lb.pingme.repository.entity.GroupUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IGroupUserDAO extends JpaSpecificationExecutor<GroupUserEntity>,
        JpaRepository<GroupUserEntity, Long> {


    List<GroupUserEntity> findAllByGroupIdAndStatusOrderByIdDesc(String groupId, Integer status);


    List<GroupUserEntity> findAllByUserIdAndStatusOrderByIdDesc(String userId, Integer status);
}
