package com.shchuko.corporatemessenger.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
public class BaseEntityWithStatus extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EntityStatus status;
}
