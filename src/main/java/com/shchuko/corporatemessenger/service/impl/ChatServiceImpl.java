package com.shchuko.corporatemessenger.service.impl;

import com.shchuko.corporatemessenger.model.Chat;
import com.shchuko.corporatemessenger.repository.ChatRepository;
import com.shchuko.corporatemessenger.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {
    private ChatRepository chatRepository;
//
//    private UserRepository userRepository;
//
//    private MessageRepository messageRepository;

    @Autowired
    public void setChatRepository(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }
//
//    @Autowired
//    public void setUserRepository(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Autowired
//    public void setMessageRepository(MessageRepository messageRepository) {
//        this.messageRepository = messageRepository;
//    }

    @Override
    public Chat getChatByName(String chatName) {
        return chatRepository.findChatByName(chatName);
    }

    @Override
    public Chat getChatById(long chatId) {
        return chatRepository.findChatById(chatId);
    }

    @Override
    public Chat createNewChat(Chat chat) {
        return chatRepository.save(chat);
    }

    @Override
    public Chat updateChat(Chat chat) {
        return chatRepository.save(chat);
    }
}
