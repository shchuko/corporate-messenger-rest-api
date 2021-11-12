package com.shchuko.corporatemessenger.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shchuko
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordUpdateRequestDTO {
    private String oldPassword;
    private String newPassword;
}
