package com.lb.pingme.repository.dao;

import com.lb.pingme.repository.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IUserDAO extends JpaSpecificationExecutor<UserEntity>, JpaRepository<UserEntity, Long> {

    /***
     * 根据手机号查询用户信息
     * @param mobile
     * @return
     */
    UserEntity findByMobile(String mobile);

    UserEntity findByUserId(String userId);

    List<UserEntity> findByUserIdIn(List<String> userId);

    UserEntity findByUserName(String userName);

    Page<UserEntity> findAllByRoleCode(Integer roleCode, Pageable pageable);


    List<UserEntity> findAllByRoleCodeOrderByIdDesc(Integer roleCode);

    /***
     * 根据手机号+密码查询用户信息，登录场景
     * @param mobile
     * @param password
     * @return
     */
    UserEntity findByMobileAndPassword(String mobile, String password);

    @Query(value = "select count(*) from coder_util_user t where t.create_date = now()", nativeQuery = true)
    Long todayCount();

    @Query(value = "select u from UserEntity u where (u.createDate between ?1 and ?2) or (u.updateDate between ?1 and ?2)")
    List<UserEntity> findAllByUpdateDateBetween(Date start, Date end);
}
