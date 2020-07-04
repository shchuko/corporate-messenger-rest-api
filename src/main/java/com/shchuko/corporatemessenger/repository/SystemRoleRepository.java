package com.shchuko.corporatemessenger.repository;

import com.shchuko.corporatemessenger.model.Role;
import com.shchuko.corporatemessenger.model.RoleTypes;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author shchuko
 */
public interface SystemRoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleTypes name);

    Role findById(Role name);
}
