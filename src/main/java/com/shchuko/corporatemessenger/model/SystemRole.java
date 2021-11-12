package com.shchuko.corporatemessenger.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "role")
@Data
public class SystemRole extends BaseEntityWithStatus {
    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 25)
    SystemRoleTypes name;
}
