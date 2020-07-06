package com.shchuko.corporatemessenger.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author shchuko
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingUpRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
