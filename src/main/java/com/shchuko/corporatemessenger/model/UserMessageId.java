package com.shchuko.corporatemessenger.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author shchuko
 */
@Data
public class UserMessageId implements Serializable {
    private long userId;

    private long messageId;
}
