package com.shchuko.corporatemessenger.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * @author shchuko
 */
@MappedSuperclass
@Data
public class BaseEntityWithStatus extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EntityStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BaseEntityWithStatus baseEntityWithStatus = (BaseEntityWithStatus) o;
        return Objects.equals(this.getId(), baseEntityWithStatus.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
