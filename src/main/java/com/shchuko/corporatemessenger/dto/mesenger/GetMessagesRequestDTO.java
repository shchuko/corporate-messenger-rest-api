package com.shchuko.corporatemessenger.dto.mesenger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author shchuko
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMessagesRequestDTO {
    private String action;
    private String chatName;
    private Date timestamp;

    public enum SupportedActions {
        GET_MESSAGES_BEFORE_TIMESTAMP,
        GET_MESSAGES_AFTER_TIMESTAMP
    }
}
