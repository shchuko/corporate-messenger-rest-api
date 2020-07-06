package com.shchuko.corporatemessenger.repository;

import com.shchuko.corporatemessenger.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author shchuko
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
    Message findById(long id);

    List<Message> findDistinctByAuthorIdOrderByTimeStampDesc(long authorId);

    List<Message> findDistinctByChatIdOrderByTimeStampDesc(long chatId);
}
