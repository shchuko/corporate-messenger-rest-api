package com.shchuko.corporatemessenger.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

/**
 * @author shchuko
 */
@Entity
@Table(name = "message", schema = "public")
@Data
@EqualsAndHashCode(callSuper = true)
public class Message extends BaseEntityWithStatus {
    @Column(name = "chat_id", insertable = false, updatable = false)
    private long chatId;

    @Column(name = "author_id")
    private long authorId;

    @CreatedDate
    @Column(name = "time_stamp")
    private Date timeStamp;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private Chat chat;
}
