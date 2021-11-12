package com.shchuko.corporatemessenger.rest;

import com.shchuko.corporatemessenger.dto.LoginRequestDTO;
import com.shchuko.corporatemessenger.dto.SingUpRequestDTO;
import com.shchuko.corporatemessenger.dto.TokenUpdateRequestDTO;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shchuko
 */
@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthenticationsRESTControllerV1 {
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

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO requestDTO) {
        try {
            String username = requestDTO.getUsername();
            String password = requestDTO.getPassword();

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            return generateTokens(username);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password", e);
        }
    }

    @PostMapping("sign-up")
    public ResponseEntity<?> signUp(@RequestBody SingUpRequestDTO requestDTO) {
        try {
            String username = requestDTO.getUsername();
            String password = requestDTO.getPassword();

            if (userService.findByUsername(username) != null) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            if (username.isEmpty() || password.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }

            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(password);

            userService.register(user);

            return login(new LoginRequestDTO(username, password));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password", e);
        }
    }

    @PostMapping("tokens-refresh")
    public ResponseEntity<?> getNewTokens(@RequestBody TokenUpdateRequestDTO requestDTO, HttpServletRequest request, Authentication authentication) {
        String sessionToken = requestDTO.getOldSessionToken();
        String refreshToken = jwtTokenProvider.resolveTokenFromRequestHeader(request);

        String sessionTokenUsername = jwtTokenProvider.getTokenUsername(sessionToken);
        String refreshTokenUsername = jwtTokenProvider.getTokenUsername(refreshToken);

        // If refreshTokenUsername != sessionTokenUsername then sessionTokenUsername is not related to this user
        // and we can't invalidate it
        // Otherwise, adding it into the blacklist for further requests
        if (refreshTokenUsername.equals(sessionTokenUsername) && jwtTokenProvider.validateAnyToken(sessionToken)) {
            // sessionToken can be invalidated (moved to blacklist)
            Date expiresOn = jwtTokenProvider.getTokenExpiration(sessionToken);
        }

        // If required, generating new refresh token and invalidating old one adding it into the
        // blacklist for further requests
        if (requestDTO.isNewRefreshTokenNeeded() && jwtTokenProvider.validateAnyToken(refreshTokenUsername)) {
            // refreshToken can be invalidated (moved to blacklist)
            Date expiresOn = jwtTokenProvider.getTokenExpiration(refreshToken);
        }

        return generateTokens(authentication.getName(), requestDTO.isNewRefreshTokenNeeded());
    }

    private ResponseEntity<?> generateTokens(String username) {
        return generateTokens(username, true);
    }

    private ResponseEntity<?> generateTokens(String username, boolean generateNewRefreshToken) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found " + username);
        }

        Map<Object, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("sessionToken", jwtTokenProvider.createSessionToken(username));

        if (generateNewRefreshToken) {
            response.put("refreshToken", jwtTokenProvider.createRefreshToken(username));
        }

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
