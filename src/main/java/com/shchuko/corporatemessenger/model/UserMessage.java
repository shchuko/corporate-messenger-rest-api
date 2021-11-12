package com.shchuko.corporatemessenger.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user_message")
@IdClass(UserMessageId.class)
public class UserMessage {
    @Column(name = "user_id")
    @Id
    private long userId;

    @Column(name = "message_id")
    @Id
    private long messageId;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "is_received")
    private boolean isReceived;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "message_id", referencedColumnName = "id")
    private Message message;
}
