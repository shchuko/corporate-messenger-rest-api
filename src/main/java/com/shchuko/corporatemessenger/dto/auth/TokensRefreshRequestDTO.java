package com.shchuko.corporatemessenger.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shchuko
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokensRefreshRequestDTO {
    String oldSessionToken;
    boolean newRefreshTokenNeeded;
}
