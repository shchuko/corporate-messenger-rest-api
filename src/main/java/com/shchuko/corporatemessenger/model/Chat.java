package com.shchuko.corporatemessenger.model;

import lombok.Data;

import javax.persistence.*;
import java.util.*;

/**
 * @author shchuko
 */
@Entity
@Table(name = "chat", schema = "public")
@Data
public class Chat extends BaseEntityWithStatus {
    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_chat",
            joinColumns = {@JoinColumn(name = "chat_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})

    private Set<User> members;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @OrderBy(value = "timeStamp")
    private List<Message> messages;

    @PostLoad
    private void postLoad() {
        if (members == null) {
            this.members = new HashSet<>();
        }

        if (messages == null) {
            this.messages = new ArrayList<>();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Chat chat = (Chat) o;
        return Objects.equals(this.getId(), chat.getId());
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
