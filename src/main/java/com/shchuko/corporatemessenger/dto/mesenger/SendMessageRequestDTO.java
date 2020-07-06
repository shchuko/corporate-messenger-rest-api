package com.shchuko.corporatemessenger.dto.mesenger;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shchuko
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequestDTO {
    private String chatName;
    private String messageContent;
}
