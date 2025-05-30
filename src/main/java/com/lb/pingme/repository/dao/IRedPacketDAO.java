package com.lb.pingme.repository.dao;

import com.lb.pingme.repository.entity.RedPacketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IRedPacketDAO extends JpaSpecificationExecutor<RedPacketEntity>,
        JpaRepository<RedPacketEntity, Long> {

}