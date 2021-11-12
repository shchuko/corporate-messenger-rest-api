package com.shchuko.corporatemessenger.dto.mesenger;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author shchuko
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserChatsResponseDTO {
    private List<ChatDTO> userChats;
}
