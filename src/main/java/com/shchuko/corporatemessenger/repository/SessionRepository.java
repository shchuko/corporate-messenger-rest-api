package com.shchuko.corporatemessenger.repository;

import com.shchuko.corporatemessenger.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByUserIdEquals(long userId);
}
