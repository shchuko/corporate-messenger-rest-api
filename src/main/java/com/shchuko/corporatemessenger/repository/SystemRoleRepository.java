package com.shchuko.corporatemessenger.repository;

import com.shchuko.corporatemessenger.model.SystemRole;
import com.shchuko.corporatemessenger.model.SystemRoleTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemRoleRepository extends JpaRepository<SystemRole, Long> {
    SystemRole findByName(SystemRoleTypes name);

    SystemRole findById(SystemRole name);
}
