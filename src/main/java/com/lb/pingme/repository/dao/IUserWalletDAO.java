package com.lb.pingme.repository.dao;

import com.lb.pingme.repository.entity.UserWalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface IUserWalletDAO extends JpaSpecificationExecutor<UserWalletEntity>,
        JpaRepository<UserWalletEntity, Long> {

    @Query(value = "select sum(uw.transType * uw.money) from UserWalletEntity uw where uw.userId = :userId")
    BigDecimal findBalanceByUserId(String userId);
}