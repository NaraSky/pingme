package com.lb.pingme.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 为所有继承它的实体类提供统一的更新时间字段和逻辑
 */
@Data
@MappedSuperclass   // JPA 注解，表明这个类是一个“映射超类”，其字段会被继承到子类实体中，但它本身不会映射成数据库表。
public class BaseEntity extends SimpleBaseEntity implements Serializable {

    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "update_date")
    private Date updateDate;

    @PreUpdate  // 更新时间字段自动更新
    public void preUpdate() {
        this.updateDate = new Date();
    }

}
