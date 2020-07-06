package com.shchuko.corporatemessenger.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author shchuko
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokensRefreshRequestDTO {
    @NotBlank
    String oldSessionToken;

    @NotNull
    Boolean newRefreshTokenNeeded;
}
