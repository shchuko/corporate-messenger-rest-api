package com.shchuko.corporatemessenger.rest;

import com.shchuko.corporatemessenger.dto.auth.*;
import com.shchuko.corporatemessenger.model.User;
import com.shchuko.corporatemessenger.security.jwt.JWTTokenProvider;
import com.shchuko.corporatemessenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author shchuko
 */
@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthenticationsRESTControllerV1 {
    private static final String LOGIN_ENDPOINT = "login";
    private static final String SIGN_UP_ENDPOINT = "sign-up";
    private static final String TOKENS_REFRESH_ENDPOINT = "tokens-refresh";
    private static final String PASSWORD_UPDATE_ENDPOINT = "password-update";

    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;
    private UserService userService;

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwtTokenProvider(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(LOGIN_ENDPOINT)
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO requestDTO) {
        try {
            String username = requestDTO.getUsername();
            String password = requestDTO.getPassword();

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            return generateTokens(username);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping(SIGN_UP_ENDPOINT)
    public ResponseEntity<?> signUp(@RequestBody @Valid SingUpRequestDTO requestDTO) {
        String username = requestDTO.getUsername();
        String password = requestDTO.getPassword();

        if (userService.findByUsername(username) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (username.isEmpty() || password.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);

        userService.register(user);

        return login(new LoginRequestDTO(username, password));
    }

    @PostMapping(TOKENS_REFRESH_ENDPOINT)
    public ResponseEntity<?> getNewTokens(@RequestBody @Valid TokensRefreshRequestDTO requestDTO, HttpServletRequest request, Authentication authentication) {
        return generateTokens(authentication.getName(), requestDTO.getNewRefreshTokenNeeded());
    }

    @PutMapping(PASSWORD_UPDATE_ENDPOINT)
    public ResponseEntity<?> passwordUpdate(@RequestBody @Valid PasswordUpdateRequestDTO requestDTO, Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName());

            String oldPassword = requestDTO.getOldPassword();
            String newPassword = requestDTO.getNewPassword();

            if (newPassword.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            if (newPassword.equals(oldPassword) || userService.updatePassword(user, oldPassword, newPassword)) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password", e);
        }
    }

    private ResponseEntity<?> generateTokens(String username) {
        return generateTokens(username, true);
    }

    private ResponseEntity<?> generateTokens(String username, boolean getNewRefreshToken) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String sessionToken = jwtTokenProvider.createSessionToken(username);
        String refreshToken = getNewRefreshToken ? jwtTokenProvider.createRefreshToken(username) : null;

        GeneratedTokensDTO response = new GeneratedTokensDTO();
        response.setUsername(username);

        response.setSessionToken(sessionToken);
        response.setSessionExpiresOn(jwtTokenProvider.getTokenExpiration(sessionToken).getTime() / 1000);

        response.setRefreshToken(refreshToken);
        response.setRefreshExpiresOn(getNewRefreshToken ? jwtTokenProvider.getTokenExpiration(refreshToken).getTime() / 1000 : 0);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
