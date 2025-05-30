package com.lb.pingme.repository.dao;

import com.lb.pingme.repository.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface IMessageDAO extends JpaRepository<MessageEntity, Long>, JpaSpecificationExecutor<MessageEntity> {

    @Modifying
    @Transactional
    @Query(value = "update web_chat_moment_message m set m.state = 1 where m.to_user_id = ?1 and m.category = ?2", nativeQuery = true)
    void updateUserMessageReadState(String toUserId, String category);
}
