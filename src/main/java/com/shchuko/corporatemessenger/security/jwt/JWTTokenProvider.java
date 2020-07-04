package com.shchuko.corporatemessenger.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

/**
 * @author shchuko
 */
@Component
public class JWTTokenProvider {
    private static final String REFRESH_TOKEN_FLAG_KEY = "refresh";

    private String encodedSecret;
    private long sessionValidTimeMillis;
    private long refreshValidTimeMillis;

    private UserDetailsService userDetailsService;

    @Autowired
    public void setEncodedSecret(@Value("${jwt.token.secret}") String secret) {
        this.encodedSecret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    @Autowired
    public void setSessionValidTimeMillis(@Value("${jwt.token.valid-time-millis}") long sessionValidTimeMillis) {
        this.sessionValidTimeMillis = sessionValidTimeMillis;
    }

    @Autowired
    public void setRefreshValidTimeMillis(@Value("${jwt.token.refresh-valid-time-millis}") long refreshValidTimeMillis) {
        this.refreshValidTimeMillis = refreshValidTimeMillis;
    }

    @Autowired
    public void setUserDetailsService(@Qualifier("JwtUserDetailsService") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Get used password encoder
     * @return Used password encoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Create session token with short valid period {@link #setSessionValidTimeMillis(long)}
     * @param username Existing user username
     * @return Created token
     */
    public String createSessionToken(String username) {
        return createToken(username, encodedSecret, sessionValidTimeMillis, false);
    }

    /**
     * Create session token with long valid period {@link #setRefreshValidTimeMillis(long)}
     * @param username Existing user username
     * @return Created token
     */
    public String createRefreshToken(String username) {
        return createToken(username, encodedSecret, refreshValidTimeMillis, true);
    }

    /**
     * Create {@link Authentication} from token
     * @param token Token
     * @return {@link Authentication} instance or <code>null</code> if not creatable
     */
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(parseTokenBody(token).getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * Resolve token from request header (bearer auth)
     * @param request {@link HttpServletRequest} which header contains token
     * @return Token string or <code>null</code> if not resolvable
     */
    public String resolveTokenFromRequestHeader(HttpServletRequest request) {
        final String headerName = "Authorization";
        final String bearer = "Bearer ";

        String bearerToken = request.getHeader(headerName);
        if (bearerToken == null || !bearerToken.startsWith(bearer)) {
            return null;
        }

        return bearerToken.substring(bearer.length());
    }

    /**
     * Validate the token by its expiration time and token blacklist
     * @param token Token to validate
     * @return <code>true</code> if valid, <code>false</code> if not
     */
    public boolean validateAnyToken(String token) {
        if (token == null) {
            return false;
        }

        Date now = new Date();
        try {
            Claims tokenBody = parseTokenBody(token);
            return tokenBody.getExpiration().after(now);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Validate refresh token by its expiration time and token blacklist
     * @param token Token to validate
     * @return <code>true</code> if valid and has 'refresh'='true' attribute, <code>false</code> if not
     */
    public boolean validateRefreshToken(String token) {
        try {
            return validateAnyToken(token) && isRefreshToken(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Checks is the given token is refresh token
     * @param token Token
     * @return <code>true</code> if refresh token, <code>false</code> if not
     */
    public boolean isRefreshToken(String token) {
        try {
            return parseTokenBody(token).get(REFRESH_TOKEN_FLAG_KEY).equals(true);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Parses token to get it expiration time
     * @param token Token
     * @return Token expiration or <code>null</code> if unparseable
     */
    public Date getTokenExpiration(String token) {
        try {
            return parseTokenBody(token).getExpiration();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Parses token to get stored username
     * @param token Token
     * @return Token expiration or <code>null</code> if unparseable
     */
    public String getTokenUsername(String token) {
        try {
            return parseTokenBody(token).getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Get the {@link Claims} from token
     * @param token Token
     * @return {@link Claims}
     */
    private Claims parseTokenBody(String token) {
        return Jwts.parser().setSigningKey(encodedSecret).parseClaimsJws(token).getBody();
    }



    /**
     * Generate new token
     * @param username Username
     * @param encodedSecret Base64-encoded JWT secret
     * @param validTimeMillis Token valid time in millisecond, valid period starts from NOW
     * @param isRefreshToken 'refresh' token attribute value
     * @return Generated token
     */
    private String createToken(String username, String encodedSecret, long validTimeMillis, boolean isRefreshToken) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(REFRESH_TOKEN_FLAG_KEY, isRefreshToken);

        Date now = new Date();
        Date expiresOn = new Date(now.getTime() + validTimeMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiresOn)
                .signWith(SignatureAlgorithm.HS256, encodedSecret)
                .compact();
    }
}
