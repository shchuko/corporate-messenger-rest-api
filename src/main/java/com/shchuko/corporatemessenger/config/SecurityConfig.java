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

    private static final String API_ENDPOINT_PREFIX = "/api/v1";

    private static final String LOGIN_ENDPOINT = API_ENDPOINT_PREFIX + "/auth/login";
    private static final String SIGN_UP_ENDPOINT = API_ENDPOINT_PREFIX + "/auth/sing-up";
    private static final String TOKEN_REFRESH_ENDPOINT = API_ENDPOINT_PREFIX + "/auth/tokens-refresh";

    private static final String ADMIN_ENDPOINT = API_ENDPOINT_PREFIX + "/admin/**";
    private static final String MESSENGER_ENDPOINT = API_ENDPOINT_PREFIX + "/messenger/**";

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
                .antMatchers(LOGIN_ENDPOINT).permitAll()
                .antMatchers(SIGN_UP_ENDPOINT).permitAll()
                .antMatchers(ADMIN_ENDPOINT).hasRole(RoleTypes.ROLE_ADMIN.getShorten())
                .antMatchers(MESSENGER_ENDPOINT).hasRole(RoleTypes.ROLE_USER.getShorten())
            .and()
                .addFilterBefore(new JWTHasRefreshTokenFilter(tokenProvider), BasicAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(TOKEN_REFRESH_ENDPOINT).authenticated();
    }
}
