package com.lb.pingme.repository.dao;

import com.lb.pingme.repository.entity.MessageTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IMessageTemplateDAO extends JpaRepository<MessageTemplateEntity, Long>,
        JpaSpecificationExecutor<MessageTemplateEntity> {

    MessageTemplateEntity findAllByCode(String code);
}
