package com.shchuko.corporatemessenger.service;

import com.shchuko.corporatemessenger.model.Chat;
import com.shchuko.corporatemessenger.model.Message;
import com.shchuko.corporatemessenger.model.User;

import java.util.List;

/**
 * @author shchuko
 */
public interface ChatService {
    Chat getChatByName(String chatName);

    Chat getChatById(long chatId);

    Chat createNewChat(Chat chat);

    Chat updateChat(Chat chat);
}
