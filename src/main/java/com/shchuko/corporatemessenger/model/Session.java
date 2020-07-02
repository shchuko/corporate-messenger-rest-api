package com.shchuko.corporatemessenger.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "session")
@Data
public class Session extends BaseEntity {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "token")
    private String token;

    @Column(name = "refresh_token")
    private String refresh_token;

    @Column(name = "expires_on")
    private Date expiresOn;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
