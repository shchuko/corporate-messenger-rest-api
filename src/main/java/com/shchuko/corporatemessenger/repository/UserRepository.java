package com.shchuko.corporatemessenger.repository;

import com.shchuko.corporatemessenger.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author shchuko
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findById(long id);
}
