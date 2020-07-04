package com.shchuko.corporatemessenger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shchuko
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingUpRequestDTO {
    private String username = "";
    private String password = "";
}
