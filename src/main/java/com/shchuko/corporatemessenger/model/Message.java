package com.shchuko.corporatemessenger.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * @author shchuko
 */
@Entity
@Table(name = "message", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Message message = (Message) o;
        return Objects.equals(this.getId(), message.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    @Override
    public String toString() {
        return this.getClass().getCanonicalName() + super.toString();
    }
}
