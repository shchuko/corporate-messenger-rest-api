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
public class GeneratedTokensDTO {
    String sessionToken;
    long sessionExpiresOn;

    String refreshToken;
    long refreshExpiresOn;

    String username;

}
