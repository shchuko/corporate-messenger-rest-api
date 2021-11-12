package com.shchuko.corporatemessenger.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author shchuko
 */
public class JWTHasSessionTokenFilter extends GenericFilterBean {
    private final JWTTokenProvider tokenProvider;

    public JWTHasSessionTokenFilter(JWTTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * Sets authentication to user which valid token was passed as a request part
     * @param request  The request to process
     * @param response The response associated with the request
     * @param chain    Provides access to the next filter in the chain for this
     *                 filter to pass the request and response to for further
     *                 processing
     * @throws IOException      if an I/O error occurs during this filter's
     *                          processing of the request
     * @throws ServletException if the processing fails for any other reason
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = tokenProvider.resolveTokenFromRequestHeader((HttpServletRequest) request);
        if (token != null && tokenProvider.validateAnyToken(token) && !tokenProvider.isRefreshToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}
