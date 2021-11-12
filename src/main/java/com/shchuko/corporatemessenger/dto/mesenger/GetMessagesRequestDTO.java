package com.shchuko.corporatemessenger.dto.mesenger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author shchuko
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMessagesRequestDTO {
    @NotBlank
    private String action;

    @NotBlank
    private String chatName;

    @NotNull
    private Long timestamp;

    public enum SupportedActions {
        GET_MESSAGES_BEFORE_TIMESTAMP,
        GET_MESSAGES_AFTER_TIMESTAMP,
        GET_ALL_MESSAGES
    }
}
