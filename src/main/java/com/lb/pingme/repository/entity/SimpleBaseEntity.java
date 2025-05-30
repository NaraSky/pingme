package com.lb.pingme.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Version;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass   // JPA 注解，表明这个类是一个“映射超类”，其字段会被继承到子类实体中，但它本身不会映射成数据库表。
public class SimpleBaseEntity implements Serializable {

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "create_date")
    private Date createDate;

    @Version    // 乐观锁控制字段，JPA 的 @Version 注解表示该字段会随着数据更新自动递增，用于防止并发更新冲突。
    @Column(name = "version")
    private Integer version;

    @PrePersist // @PrePersist：JPA 生命周期注解，表示在实体首次持久化（保存）前执行。
    public void prePersist() {
        Date now = new Date();
        if (this.createDate == null) {
            this.createDate = now;
        }
    }
}
