package com.shchuko.corporatemessenger.repository;

import com.shchuko.corporatemessenger.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);

    User findById(long id);

    List<User> findAllByPasswordHash(String passwordHash);
}
