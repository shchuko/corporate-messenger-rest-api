package com.shchuko.corporatemessenger.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "chat")
@Data
public class Chat extends BaseEntityWithStatus {
    @Column(name = "name")
    private String name;

    @ManyToMany
    @JoinTable(name = "user_chat",
            joinColumns = {@JoinColumn(name = "chat_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
    private List<User> members;

    @OneToMany(mappedBy = "chat")
    private List<Message> messages;


}
