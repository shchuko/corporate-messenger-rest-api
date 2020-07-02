package com.shchuko.corporatemessenger.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
@Data
public class User extends BaseEntityWithStatus {
    @Column(name = "login", unique = true)
    private String login;

    @Column(name = "password_hash")
    private String passwordHash;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<SystemRole> roles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_chat",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "chat_id", referencedColumnName = "id")})
    private List<Chat> chats;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Session> sessions;

    @ManyToMany
    @JoinTable(name = "user_message",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "message_id", referencedColumnName = "id")})
    private List<Message> messages;
}
