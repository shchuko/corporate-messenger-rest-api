package com.shchuko.corporatemessenger.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

/**
 * @author shchuko
 */
@Entity
@Table(name = "role", schema = "public")
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntityWithStatus {
    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 25)
    RoleTypes name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
    List<User> users;
}
