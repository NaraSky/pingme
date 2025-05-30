package com.lb.pingme.repository.dao;

import com.lb.pingme.repository.entity.ChatMessEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// 聊天消息数据访问接口，提供聊天消息相关的数据库操作
@Repository
public interface IChatMessDAO extends JpaSpecificationExecutor<ChatMessEntity>,
        JpaRepository<ChatMessEntity, Long> {

    // 根据发送者和接收者查询聊天记录，按消息ID降序排列
    @Query(value = "select distinct mess from ChatMessEntity mess where "
            + "(mess.sender = :sender or mess.receiver = :receiver) and "
            + "(mess.receiver = :sender or mess.sender = :receiver) order by mess.id desc")
    List<ChatMessEntity> findAllBySenderAndReceiver(String sender, String receiver);

    // 分页查询包含指定关键词的消息内容
    Page<ChatMessEntity> findAllByMessageLike(String message, Pageable pageable);

}