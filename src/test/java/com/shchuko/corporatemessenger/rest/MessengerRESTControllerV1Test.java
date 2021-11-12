package com.shchuko.corporatemessenger.rest;

import com.shchuko.corporatemessenger.dto.mesenger.*;
import com.shchuko.corporatemessenger.model.Chat;
import com.shchuko.corporatemessenger.model.Message;
import com.shchuko.corporatemessenger.model.User;
import com.shchuko.corporatemessenger.service.ChatService;
import com.shchuko.corporatemessenger.service.MessageService;
import com.shchuko.corporatemessenger.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;

@AutoConfigureMockMvc
public class MessengerRESTControllerV1Test {
    private static final String testUsername = "testUsername";

    private MessengerRESTControllerV1 controller;

    private ChatService chatService;

    private MessageService messageService;

    private UserService userService;

    private Authentication authentication;

    @BeforeEach
    void beforeEach() {
        controller = new MessengerRESTControllerV1();

        chatService = Mockito.mock(ChatService.class);
        messageService = Mockito.mock(MessageService.class);
        userService = Mockito.mock(UserService.class);

        controller.setUserService(userService);
        controller.setMessageService(messageService);
        controller.setChatService(chatService);

        authentication = Mockito.mock(Authentication.class);
        Mockito.doReturn(testUsername).when(authentication).getName();
    }

    @Test
    void testGetMessages() {
        String chatName = "testChat";
        Date timestamp = new Date();
        Chat chat = new Chat();
        User user = new User(testUsername, null, null, null, null);
        long authorId = 42;
        long chatId = 42;

        chat.setName(chatName);
        chat.setMembers(new HashSet<>(Collections.singletonList(user)));
        chat.setId(chatId);

        List<Message> messages = new ArrayList<>();
        messages.add(new Message(chatId, authorId, timestamp, "somecontent", chat));
        messages.add(new Message(chatId, authorId, timestamp, "somecontent2", chat));
        messages.add(new Message(chatId, authorId, timestamp, "somecontent3", chat));
        messages.add(new Message(chatId, authorId, timestamp, "somecontent4", chat));
        chat.setMessages(messages);

        List<MessageDTO> expectedMessages = chat.getMessages().stream().map(message ->
                        new MessageDTO(user.getUsername(),
                                message.getContent(),
                                message.getTimeStamp().getTime()))
                .collect(Collectors.toList());

        Mockito.when(chatService.getChatByName(chatName)).thenReturn(chat);
        Mockito.when(chatService.getChatById(chatId)).thenReturn(chat);
        Mockito.when(userService.findByUsername(testUsername)).thenReturn(user);
        Mockito.when(userService.findById(authorId)).thenReturn(user);

        ResponseEntity<GetMessagesResponseDTO> result = controller.getMessages(new GetMessagesRequestDTO(
                GetMessagesRequestDTO.SupportedActions.GET_ALL_MESSAGES.name(),
                chatName,
                (long) -1), authentication);

        assertThat(result.getStatusCode()).isEqualTo(ResponseEntity.ok().build().getStatusCode());
        assertThat(Objects.requireNonNull(Objects.requireNonNull(result.getBody()).getMessages())).isEqualTo(expectedMessages);
    }

    @Test
    void testGetMessagesBeforeTimestamp() {
        String chatName = "testChat";
        Date timestamp1 = new Date(100);
        Date timestampBetween = new Date(500);
        Date timestamp2 = new Date(1000);
        Chat chat = new Chat();
        User user = new User(testUsername, null, null, null, null);
        long authorId = 42;
        long chatId = 42;

        chat.setName(chatName);
        chat.setMembers(new HashSet<>(Collections.singletonList(user)));
        chat.setId(chatId);

        List<Message> messages = new ArrayList<>();
        messages.add(new Message(chatId, authorId, timestamp1, "somecontent", chat));
        messages.add(new Message(chatId, authorId, timestamp1, "somecontent2", chat));
        messages.add(new Message(chatId, authorId, timestamp2, "somecontent3", chat));
        messages.add(new Message(chatId, authorId, timestamp2, "somecontent4", chat));
        chat.setMessages(messages);

        List<MessageDTO> expectedMessages = chat.getMessages().stream()
                .filter(message -> message.getTimeStamp().before(timestampBetween))
                .map(message -> new MessageDTO(user.getUsername(),
                        message.getContent(),
                        message.getTimeStamp().getTime()))
                .collect(Collectors.toList());

        Mockito.when(chatService.getChatByName(chatName)).thenReturn(chat);
        Mockito.when(chatService.getChatById(chatId)).thenReturn(chat);
        Mockito.when(userService.findByUsername(testUsername)).thenReturn(user);
        Mockito.when(userService.findById(authorId)).thenReturn(user);

        ResponseEntity<GetMessagesResponseDTO> result = controller.getMessages(new GetMessagesRequestDTO(
                GetMessagesRequestDTO.SupportedActions.GET_MESSAGES_BEFORE_TIMESTAMP.name(),
                chatName,
                timestampBetween.getTime()), authentication);

        assertThat(result.getStatusCode()).isEqualTo(ResponseEntity.ok().build().getStatusCode());
        assertThat(Objects.requireNonNull(Objects.requireNonNull(result.getBody()).getMessages())).isEqualTo(expectedMessages);
    }

    @Test
    void testGetMessagesBeforeTimestampBoundary() {
        String chatName = "testChat";
        Date timestamp1 = new Date(100);
        Date timestampBetween = new Date(500);
        Date timestamp2 = new Date(500);
        Date timestamp3 = new Date(1000);
        Chat chat = new Chat();
        User user = new User(testUsername, null, null, null, null);
        long authorId = 42;
        long chatId = 42;

        chat.setName(chatName);
        chat.setMembers(new HashSet<>(Collections.singletonList(user)));
        chat.setId(chatId);

        List<Message> messages = new ArrayList<>();
        messages.add(new Message(chatId, authorId, timestamp1, "somecontent", chat));
        messages.add(new Message(chatId, authorId, timestamp1, "somecontent2", chat));
        messages.add(new Message(chatId, authorId, timestamp2, "somecontent3", chat));
        messages.add(new Message(chatId, authorId, timestamp3, "somecontent4", chat));
        chat.setMessages(messages);

        List<MessageDTO> expectedMessages = chat.getMessages().stream()
                .filter(message -> message.getTimeStamp().before(timestampBetween))
                .map(message -> new MessageDTO(user.getUsername(),
                        message.getContent(),
                        message.getTimeStamp().getTime()))
                .collect(Collectors.toList());

        Mockito.when(chatService.getChatByName(chatName)).thenReturn(chat);
        Mockito.when(chatService.getChatById(chatId)).thenReturn(chat);
        Mockito.when(userService.findByUsername(testUsername)).thenReturn(user);
        Mockito.when(userService.findById(authorId)).thenReturn(user);

        ResponseEntity<GetMessagesResponseDTO> result = controller.getMessages(new GetMessagesRequestDTO(
                GetMessagesRequestDTO.SupportedActions.GET_MESSAGES_BEFORE_TIMESTAMP.name(),
                chatName,
                timestampBetween.getTime()), authentication);

        assertThat(result.getStatusCode()).isEqualTo(ResponseEntity.ok().build().getStatusCode());
        assertThat(Objects.requireNonNull(Objects.requireNonNull(result.getBody()).getMessages())).isEqualTo(expectedMessages);
    }

    @Test
    void testGetMessagesAfterTimestamp() {
        String chatName = "testChat";
        Date timestamp1 = new Date(100);
        Date timestampBetween = new Date(500);
        Date timestamp2 = new Date(1000);
        Chat chat = new Chat();
        User user = new User(testUsername, null, null, null, null);
        long authorId = 42;
        long chatId = 42;

        chat.setName(chatName);
        chat.setMembers(new HashSet<>(Collections.singletonList(user)));
        chat.setId(chatId);

        List<Message> messages = new ArrayList<>();
        messages.add(new Message(chatId, authorId, timestamp1, "somecontent", chat));
        messages.add(new Message(chatId, authorId, timestamp1, "somecontent2", chat));
        messages.add(new Message(chatId, authorId, timestamp2, "somecontent3", chat));
        messages.add(new Message(chatId, authorId, timestamp2, "somecontent4", chat));
        chat.setMessages(messages);

        List<MessageDTO> expectedMessages = chat.getMessages().stream()
                .filter(message -> message.getTimeStamp().after(timestampBetween))
                .map(message -> new MessageDTO(user.getUsername(),
                        message.getContent(),
                        message.getTimeStamp().getTime()))
                .collect(Collectors.toList());

        Mockito.when(chatService.getChatByName(chatName)).thenReturn(chat);
        Mockito.when(chatService.getChatById(chatId)).thenReturn(chat);
        Mockito.when(userService.findByUsername(testUsername)).thenReturn(user);
        Mockito.when(userService.findById(authorId)).thenReturn(user);

        ResponseEntity<GetMessagesResponseDTO> result = controller.getMessages(new GetMessagesRequestDTO(
                GetMessagesRequestDTO.SupportedActions.GET_MESSAGES_AFTER_TIMESTAMP.name(),
                chatName,
                timestampBetween.getTime()), authentication);

        assertThat(result.getStatusCode()).isEqualTo(ResponseEntity.ok().build().getStatusCode());
        assertThat(Objects.requireNonNull(Objects.requireNonNull(result.getBody()).getMessages())).isEqualTo(expectedMessages);
    }

    @Test
    void testGetMessagesAfterTimestampBoundary() {
        String chatName = "testChat";
        Date timestamp1 = new Date(100);
        Date timestampBetween = new Date(500);
        Date timestamp2 = new Date(500);
        Date timestamp3 = new Date(1000);
        Chat chat = new Chat();
        User user = new User(testUsername, null, null, null, null);
        long authorId = 42;
        long chatId = 42;

        chat.setName(chatName);
        chat.setMembers(new HashSet<>(Collections.singletonList(user)));
        chat.setId(chatId);

        List<Message> messages = new ArrayList<>();
        messages.add(new Message(chatId, authorId, timestamp1, "somecontent", chat));
        messages.add(new Message(chatId, authorId, timestamp1, "somecontent2", chat));
        messages.add(new Message(chatId, authorId, timestamp2, "somecontent3", chat));
        messages.add(new Message(chatId, authorId, timestamp3, "somecontent4", chat));
        chat.setMessages(messages);

        List<MessageDTO> expectedMessages = chat.getMessages().stream()
                .filter(message -> message.getTimeStamp().after(timestampBetween))
                .map(message -> new MessageDTO(user.getUsername(),
                        message.getContent(),
                        message.getTimeStamp().getTime()))
                .collect(Collectors.toList());

        Mockito.when(chatService.getChatByName(chatName)).thenReturn(chat);
        Mockito.when(chatService.getChatById(chatId)).thenReturn(chat);
        Mockito.when(userService.findByUsername(testUsername)).thenReturn(user);
        Mockito.when(userService.findById(authorId)).thenReturn(user);

        ResponseEntity<GetMessagesResponseDTO> result = controller.getMessages(new GetMessagesRequestDTO(
                GetMessagesRequestDTO.SupportedActions.GET_MESSAGES_AFTER_TIMESTAMP.name(),
                chatName,
                timestampBetween.getTime()), authentication);

        assertThat(result.getStatusCode()).isEqualTo(ResponseEntity.ok().build().getStatusCode());
        assertThat(Objects.requireNonNull(Objects.requireNonNull(result.getBody()).getMessages())).isEqualTo(expectedMessages);
    }

    @Test
    void testSendMessage() {
        String chatName = "testChat";
        Date timestamp1 = new Date(100);
        Chat chat = new Chat();
        User user = new User(testUsername, null, null, null, null);
        long authorId = 42;
        long chatId = 42;

        chat.setName(chatName);
        chat.setMembers(new HashSet<>(Collections.singletonList(user)));
        chat.setId(chatId);

        List<Message> messages = new ArrayList<>();

        Message testMessage = new Message(chatId, authorId, timestamp1, "content", chat);
        List<MessageDTO> expectedMessages = Stream.of(testMessage)
                .map(message -> new MessageDTO(user.getUsername(),
                        message.getContent(),
                        message.getTimeStamp().getTime()))
                .collect(Collectors.toList());


        Mockito.when(chatService.getChatByName(chatName)).thenReturn(chat);
        Mockito.when(chatService.getChatById(chatId)).thenReturn(chat);
        Mockito.when(userService.findByUsername(testUsername)).thenReturn(user);
        Mockito.when(userService.findById(authorId)).thenReturn(user);
        Mockito.when(messageService.createMessage(any(Message.class))).thenAnswer(invocation -> {
            messages.add(testMessage);
            return testMessage;
        });

        ResponseEntity<SendMessageResponseDTO> response = controller.sendMessage(new SendMessageRequestDTO(chatName, testMessage.getContent()), authentication);

        assertThat(response.getStatusCode()).isEqualTo(ResponseEntity.status(201).build().getStatusCode());

        chat.setMessages(messages);
        ResponseEntity<GetMessagesResponseDTO> result = controller.getMessages(new GetMessagesRequestDTO(
                GetMessagesRequestDTO.SupportedActions.GET_ALL_MESSAGES.name(),
                chatName,
                (long) -1), authentication);


        assertThat(result.getStatusCode()).isEqualTo(ResponseEntity.ok().build().getStatusCode());
        assertThat(Objects.requireNonNull(Objects.requireNonNull(result.getBody()).getMessages())).isEqualTo(expectedMessages);
    }

    @Test
    void TestGetUsernames() {
        List<String> usernames = Arrays.asList("a", "b", "c", "d", testUsername);
        List<User> users = usernames.stream().map(item ->
                new User(item, null, null, null, null)).collect(Collectors.toList());
        Mockito.when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<String>> result = controller.getUsernames(authentication);

        assertThat(result.getStatusCode()).isEqualTo(ResponseEntity.ok().build().getStatusCode());
        assertThat(Objects.requireNonNull(result.getBody()).toArray())
                .containsExactlyInAnyOrder(usernames.stream().filter(name -> !name.equals(testUsername)).toArray());
    }

}
