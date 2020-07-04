package com.shchuko.corporatemessenger.config;

import com.shchuko.corporatemessenger.service.JWTUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author shchuko
 */
@Configuration
public class SpringConfig {
    @Bean(name = "JwtUserDetailsService")
    public static UserDetailsService getUserDetailsService() {
        return new JWTUserDetailsService();
    }
}
