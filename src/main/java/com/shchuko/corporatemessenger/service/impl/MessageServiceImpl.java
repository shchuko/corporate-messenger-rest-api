package com.shchuko.corporatemessenger.service.impl;

import com.shchuko.corporatemessenger.model.Message;
import com.shchuko.corporatemessenger.model.User;
import com.shchuko.corporatemessenger.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shchuko.corporatemessenger.service.MessageService;

import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private MessageRepository messageRepository;

    @Autowired
    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message getMessageById(long id) {
        return messageRepository.findById(id);
    }

    @Override
    public Message createMessage(Message message) {
        message.setTimeStamp(new Date());
        return messageRepository.save(message);
    }
}
