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
public class AddChatMembersResponseDTO {
    private String statusMessage;

    public enum StatusTemplates {
        NOTHING_TO_ADD, SUCCESSFUL
    }
}
