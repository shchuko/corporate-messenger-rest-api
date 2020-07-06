package com.shchuko.corporatemessenger.dto.mesenger;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author shchuko
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequestDTO {
    @NotBlank
    private String chatName;

    @NotBlank
    private String messageContent;
}
