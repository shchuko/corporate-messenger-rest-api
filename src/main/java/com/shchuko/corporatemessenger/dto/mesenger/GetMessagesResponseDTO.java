package com.shchuko.corporatemessenger.dto.mesenger;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author shchuko
 */
@Data
public class GetMessagesResponseDTO implements Serializable {
    private String chatName;
    private List<MessageDTO> messages;
}
