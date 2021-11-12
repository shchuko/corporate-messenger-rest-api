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
public class TokenUpdateRequestDTO {
    String oldSessionToken;
    boolean newRefreshTokenNeeded;
}