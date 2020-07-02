package com.shchuko.corporatemessenger.repository;

import com.shchuko.corporatemessenger.model.UserMessage;
import com.shchuko.corporatemessenger.model.UserMessageId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMessageRepository extends JpaRepository<UserMessage, UserMessageId> {
    UserMessage findByUserIdEqualsAndMessageIdEquals(long userId, long chatId);
}
