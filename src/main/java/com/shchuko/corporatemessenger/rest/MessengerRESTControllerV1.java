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

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
    public ResponseEntity<GetMessagesResponseDTO> getMessages(@RequestBody @Valid GetMessagesRequestDTO requestDTO, Authentication authentication) {
        Chat chat = chatService.getChatByName(requestDTO.getChatName());
        if (chat == null) {
            return ResponseEntity.badRequest().build();
        }

        if (!chat.getMembers().contains(userService.findByUsername(authentication.getName()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Date checkDate = new Date(requestDTO.getTimestamp() * 1000);
        Predicate<Message> predicate;
        if (requestDTO.getAction().equals(GetMessagesRequestDTO.SupportedActions.GET_MESSAGES_AFTER_TIMESTAMP.name())) {
            predicate = message -> message.getTimeStamp().after(checkDate);
        } else if (requestDTO.getAction().equals(GetMessagesRequestDTO.SupportedActions.GET_MESSAGES_BEFORE_TIMESTAMP.name())) {
            predicate = message -> message.getTimeStamp().before(checkDate);
        } else {
            return ResponseEntity.badRequest().build();
        }

        List<MessageDTO> responseMessages = chat.getMessages().stream()
                .filter(predicate).map(message ->
                        new MessageDTO(userService.findById(message.getAuthorId()).getUsername(),
                                message.getContent(),
                                message.getTimeStamp().getTime() / 1000))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new GetMessagesResponseDTO(requestDTO.getChatName(), responseMessages));
    }

    @PostMapping(value = MESSAGES_ENDPOINT)
    public ResponseEntity<SendMessageResponseDTO> sendMessage(@RequestBody @Valid SendMessageRequestDTO requestDTO, Authentication authentication) {

        Chat chat = chatService.getChatByName(requestDTO.getChatName());
        User author = userService.findByUsername(authentication.getName());

        if (chat == null || author == null) {
            return ResponseEntity.badRequest().build();
        }

        if (!chat.getMembers().contains(userService.findByUsername(authentication.getName()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Message message = new Message();
        message.setTimeStamp(new Date());
        message.setAuthorId(author.getId());
        message.setChatId(chat.getId());
        message.setChat(chat);
        message.setStatus(EntityStatus.ACTIVE);
        message.setContent(requestDTO.getMessageContent());

        return new ResponseEntity<>(
                new SendMessageResponseDTO(messageService.createMessage(message).getTimeStamp().getTime() / 1000),
                HttpStatus.CREATED);
    }

    @GetMapping(value = CHATS_ENDPOINT)
    public ResponseEntity<GetUserChatsResponseDTO> getUserChats(Authentication authentication) {
        Set<Chat> chats = userService.findByUsername(authentication.getName()).getChats();
        List<ChatDTO> chatsDTO = chats.stream()
                .map(chat -> new ChatDTO(
                        chat.getName(),
                        chat.getMembers().stream().map(User::getUsername).collect(Collectors.toSet())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new GetUserChatsResponseDTO(chatsDTO));
    }

    @PostMapping(value = CHATS_ENDPOINT)
    public ResponseEntity<?> createChat(@RequestBody CreateChatRequestDTO requestDTO, Authentication authentication) {
        if (chatService.getChatByName(requestDTO.getChatName()) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new CreateChatResponseDTO(CreateChatResponseDTO.StatusTemplates.ALREADY_EXISTS.name()));
        }

        requestDTO.getMembers().add(authentication.getName());
        Set<User> chatMembers = requestDTO.getMembers().stream()
                .map(username -> userService.findByUsername(username))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (chatMembers.size() < 2) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new CreateChatResponseDTO(CreateChatResponseDTO.StatusTemplates.NOT_ENOUGH_MEMBERS.name()));
        }

        Chat chat = new Chat();
        chat.setName(requestDTO.getChatName());
        chat.setMembers(chatMembers);
        chat.setStatus(EntityStatus.ACTIVE);

        chatService.createNewChat(chat);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CreateChatResponseDTO(CreateChatResponseDTO.StatusTemplates.SUCCESSFUL.name()));
    }

    @PutMapping(value = CHATS_ENDPOINT)
    public ResponseEntity<AddChatMembersResponseDTO> addChatMembers(@RequestBody AddChatMembersRequestDTO requestDTO, Authentication authentication) {
        Chat chat = chatService.getChatByName(requestDTO.getChatName());
        if (chat == null) {
            return ResponseEntity.badRequest().build();
        }

        if (!chat.getMembers().contains(userService.findByUsername(authentication.getName()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        requestDTO.getMembers().add(authentication.getName());
        Set<User> oldChatMembers = chat.getMembers();
        Set<User> newChatMembers = requestDTO.getMembers()
                .stream().map(username -> userService.findByUsername(username))
                .filter(Objects::nonNull)
                .filter(user -> !oldChatMembers.contains(user)).collect(Collectors.toSet());

        if (newChatMembers.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new AddChatMembersResponseDTO(AddChatMembersResponseDTO.StatusTemplates.NOTHING_TO_ADD.name()));
        }

        if (oldChatMembers.isEmpty()) {
            chat.setMembers(newChatMembers);
        } else {
            chat.getMembers().addAll(newChatMembers);
        }

        chatService.updateChat(chat);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AddChatMembersResponseDTO(AddChatMembersResponseDTO.StatusTemplates.SUCCESSFUL.name()));

    }
}
