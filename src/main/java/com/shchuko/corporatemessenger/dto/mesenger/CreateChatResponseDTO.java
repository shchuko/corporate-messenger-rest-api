package com.shchuko.corporatemessenger.dto.mesenger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shchuko
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatResponseDTO {
    private String statusMessage;

    public enum StatusTemplates {
        ALREADY_EXISTS,
        NOT_ENOUGH_MEMBERS,
        SUCCESSFUL
    }
}
