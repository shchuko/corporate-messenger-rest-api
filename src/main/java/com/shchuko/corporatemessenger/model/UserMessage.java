package com.shchuko.corporatemessenger.model;

import lombok.Data;

import javax.persistence.*;

/**
 * @author shchuko
 */
@Data
@Entity
@Table(name = "user_message", schema = "public")
@IdClass(UserMessageId.class)
public class UserMessage {
    @Column(name = "user_id", insertable = false, updatable = false)
    @Id
    private long userId;

    @Column(name = "message_id", insertable = false, updatable = false)
    @Id
    private long messageId;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "is_received")
    private boolean isReceived;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "message_id", referencedColumnName = "id")
    private Message message;
}
