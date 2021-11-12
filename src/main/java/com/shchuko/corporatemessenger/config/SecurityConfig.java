package com.shchuko.corporatemessenger.config;

import com.shchuko.corporatemessenger.model.RoleTypes;
import com.shchuko.corporatemessenger.security.jwt.JWTHasSessionTokenFilter;
import com.shchuko.corporatemessenger.security.jwt.JWTHasRefreshTokenFilter;
import com.shchuko.corporatemessenger.security.jwt.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


/**
 * @author shchuko
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private JWTTokenProvider tokenProvider;

    private UserDetailsService userService;

    public SecurityConfig() {

    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return tokenProvider.passwordEncoder();
    }

    @Autowired
    public void setTokenProvider(JWTTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Autowired
    public void setUserService(@Qualifier("JwtUserDetailsService") UserDetailsService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .addFilterBefore(new JWTHasSessionTokenFilter(tokenProvider), BasicAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/api/v1/auth/login").permitAll()
                .antMatchers("/api/v1/auth/sing-up").permitAll()
                .antMatchers("/api/v1/admin/**").hasRole(RoleTypes.ROLE_ADMIN.getShorten())
                .antMatchers("/api/v1/messenger/**").hasRole(RoleTypes.ROLE_USER.getShorten())
                .antMatchers("/api/v1/auth/password-update").authenticated()
            .and()
                .addFilterBefore(new JWTHasRefreshTokenFilter(tokenProvider), BasicAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/api/v1/auth/tokens-refresh").authenticated();
    }
}
