package com.shchuko.corporatemessenger.repository;

import com.shchuko.corporatemessenger.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author shchuko
 */
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat findChatById(long id);

    Chat findChatByName(String name);
}
