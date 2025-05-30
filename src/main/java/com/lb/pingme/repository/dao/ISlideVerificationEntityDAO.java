package com.lb.pingme.repository.dao;

import com.lb.pingme.repository.entity.SlideVerificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISlideVerificationEntityDAO extends JpaSpecificationExecutor<SlideVerificationEntity>,
        JpaRepository<SlideVerificationEntity, Long> {

    List<SlideVerificationEntity> findAllByStatus(Integer status);

    Page<SlideVerificationEntity> findAllByStatus(Integer status, Pageable pageable);
}
