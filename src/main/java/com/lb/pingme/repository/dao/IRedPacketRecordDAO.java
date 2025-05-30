package com.lb.pingme.repository.dao;

import com.lb.pingme.repository.entity.RedPacketRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IRedPacketRecordDAO extends JpaSpecificationExecutor<RedPacketRecordEntity>,
        JpaRepository<RedPacketRecordEntity, Long> {

}