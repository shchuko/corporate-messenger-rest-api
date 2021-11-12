package com.shchuko.corporatemessenger.dto.mesenger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author shchuko
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddChatMembersRequestDTO {
    private String chatName;
    private Set<String> members;
}
