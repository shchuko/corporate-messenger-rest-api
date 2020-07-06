package com.shchuko.corporatemessenger.service;

import com.shchuko.corporatemessenger.model.Message;
import com.shchuko.corporatemessenger.model.User;

import java.util.List;

/**
 * @author shchuko
 */
public interface MessageService {
    Message getMessageById(long id);
    Message createMessage(Message message);
}
