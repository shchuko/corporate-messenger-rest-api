package com.shchuko.corporatemessenger.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * @author shchuko
 */
@Entity
@Table(name = "role", schema = "public")
@Data
public class Role extends BaseEntityWithStatus {
    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 25)
    RoleTypes name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
    List<User> users;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Role role = (Role) o;
        return Objects.equals(this.getId(), role.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    @Override
    public String toString() {
        return this.getClass().getCanonicalName() + super.toString();
    }
}
