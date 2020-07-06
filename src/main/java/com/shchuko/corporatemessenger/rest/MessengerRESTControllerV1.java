package com.shchuko.corporatemessenger.rest;

import com.shchuko.corporatemessenger.dto.mesenger.*;
import com.shchuko.corporatemessenger.model.Chat;
import com.shchuko.corporatemessenger.model.EntityStatus;
import com.shchuko.corporatemessenger.model.Message;
import com.shchuko.corporatemessenger.model.User;
import com.shchuko.corporatemessenger.service.ChatService;
import com.shchuko.corporatemessenger.service.MessageService;
import com.shchuko.corporatemessenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author shchuko
 */
@RestController
@RequestMapping(value = "/api/v1/messenger")
public class MessengerRESTControllerV1 {
    private static final String CHATS_ENDPOINT = "chats";
    private static final String MESSAGES_ENDPOINT = "messages";

    private UserService userService;

    private ChatService chatService;

    private MessageService messageService;

    public MessengerRESTControllerV1() {
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping(value = MESSAGES_ENDPOINT)
    public ResponseEntity<GetMessagesResponseDTO> getMessages(@RequestBody GetMessagesRequestDTO requestDTO, Authentication authentication) {
        if (requestDTO == null || requestDTO.getChatName() == null || requestDTO.getAction() == null) {
            return ResponseEntity.badRequest().build();
        }

        Chat chat = chatService.getChatByName(requestDTO.getChatName());

        Predicate<Message> predicate;

        if (requestDTO.getAction().equals(GetMessagesRequestDTO.SupportedActions.GET_MESSAGES_AFTER_TIMESTAMP.name())) {
            predicate = message -> message.getTimeStamp().before(requestDTO.getTimestamp());
        } else if (requestDTO.getAction().equals(GetMessagesRequestDTO.SupportedActions.GET_MESSAGES_BEFORE_TIMESTAMP.name())) {
            predicate = message -> message.getTimeStamp().after(requestDTO.getTimestamp());
        } else {
            return ResponseEntity.badRequest().build();
        }

        List<MessageDTO> responseMessages = chat.getMessages().stream()
                .filter(predicate)
                .map(message -> new MessageDTO(
                        userService.findById(message.getAuthorId()).getUsername(),
                        message.getContent(),
                        message.getTimeStamp())).collect(Collectors.toList());

        GetMessagesResponseDTO responseDTO = new GetMessagesResponseDTO();
        responseDTO.setChatName(requestDTO.getChatName());
        responseDTO.setMessages(responseMessages);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping(value = MESSAGES_ENDPOINT)
    public ResponseEntity<SendMessageResponseDTO> sendMessage(@RequestBody SendMessageRequestDTO requestDTO, Authentication authentication) {
        if (requestDTO == null || requestDTO.getChatName() == null || requestDTO.getMessageContent() == null) {
            return ResponseEntity.badRequest().build();
        }

        Chat chat = chatService.getChatByName(requestDTO.getChatName());
        User author = userService.findByUsername(authentication.getName());

        if (chat == null || author == null) {
            return ResponseEntity.noContent().build();
        }

        Message message = new Message();
        message.setTimeStamp(new Date());
        message.setAuthorId(author.getId());
        message.setChatId(chat.getId());
        message.setChat(chat);
        message.setStatus(EntityStatus.ACTIVE);
        message.setContent(requestDTO.getMessageContent());

        Message createdMessage = messageService.createMessage(message);
        if (createdMessage != null) {
            return new ResponseEntity<>(new SendMessageResponseDTO(createdMessage.getTimeStamp()), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(value = CHATS_ENDPOINT)
    public ResponseEntity<GetUserChatsResponseDTO> getUserChats(Authentication authentication) {
        Set<Chat> chats = userService.findByUsername(authentication.getName()).getChats();
        List<ChatDTO> chatsDTO = chats.stream().map(chat -> new ChatDTO(chat.getName())).collect(Collectors.toList());
        return ResponseEntity.ok(new GetUserChatsResponseDTO(chatsDTO));
    }

    @PostMapping(value = CHATS_ENDPOINT)
    public ResponseEntity<?> createChat(@RequestBody CreateChatRequestDTO requestDTO, Authentication authentication) {
        if (requestDTO == null || requestDTO.getChatName() == null || requestDTO.getMembers() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (chatService.getChatByName(requestDTO.getChatName()) != null) {
            return new ResponseEntity<>(
                    new CreateChatResponseDTO(CreateChatResponseDTO.StatusTemplates.ALREADY_EXISTS.name()),
                    HttpStatus.CONFLICT);
        }

        requestDTO.getMembers().add(authentication.getName());
        Set<User> chatMembers = requestDTO.getMembers()
                .stream().map(username -> userService.findByUsername(username))
                .filter(Objects::nonNull).collect(Collectors.toSet());

        if (chatMembers.size() < 2) {
            return new ResponseEntity<>(
                    new CreateChatResponseDTO(CreateChatResponseDTO.StatusTemplates.NOT_ENOUGH_MEMBERS.name()),
                    HttpStatus.CONFLICT);
        }
        Chat chat = new Chat();
        chat.setName(requestDTO.getChatName());
        chat.setStatus(EntityStatus.ACTIVE);
        chat.setMembers(chatMembers);

        if (chatService.createNewChat(chat) != null) {
            return new ResponseEntity<>(
                    new CreateChatResponseDTO(CreateChatResponseDTO.StatusTemplates.SUCCESSFUL.name()),
                    HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping(value = CHATS_ENDPOINT)
    public ResponseEntity<AddChatMembersResponseDTO> addChatMembers(@RequestBody AddChatMembersRequestDTO requestDTO, Authentication authentication) {
        if (requestDTO == null || requestDTO.getChatName() == null || requestDTO.getMembers() == null) {
            return ResponseEntity.badRequest().build();
        }

        Chat chat = chatService.getChatByName(requestDTO.getChatName());
        if (chat == null) {
            return ResponseEntity.badRequest().build();
        }

        requestDTO.getMembers().add(authentication.getName());
        Set<User> oldChatMembers = chat.getMembers();
        Set<User> newChatMembers = requestDTO.getMembers()
                .stream().map(username -> userService.findByUsername(username))
                .filter(Objects::nonNull)
                .filter(user -> !oldChatMembers.contains(user)).collect(Collectors.toSet());

        if (newChatMembers.isEmpty()) {
            return new ResponseEntity<>(
                    new AddChatMembersResponseDTO(AddChatMembersResponseDTO.StatusTemplates.NOTHING_TO_ADD.name()),
                    HttpStatus.CONFLICT);
        }

        if (oldChatMembers.isEmpty()) {
            chat.setMembers(newChatMembers);
        } else {
            chat.getMembers().addAll(newChatMembers);
        }

        if (chatService.updateChat(chat) != null) {
            return new ResponseEntity<>(
                    new AddChatMembersResponseDTO(AddChatMembersResponseDTO.StatusTemplates.SUCCESSFUL.name()),
                    HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

//
//    @GetMapping(value = "users/{id}")
//    public ResponseEntity<UserDTO> getUserById(@PathVariable(name = "id") long id) {
//        User user = userService.findById(id);
//        if (user == null) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//
//        return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
//    }
//
//    @GetMapping(value = "user-logins/{username}")
//    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable(name = "username") String username) {
//        User user = userService.findByUsername(username);
//        if (user == null) {
//            return ResponseEntity.noContent().build();
//        }
//
//        return ResponseEntity.ok(new UserDTO(user));
//    }

}
