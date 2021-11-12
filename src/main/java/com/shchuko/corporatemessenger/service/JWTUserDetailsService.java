package com.shchuko.corporatemessenger.service;

import com.shchuko.corporatemessenger.model.EntityStatus;
import com.shchuko.corporatemessenger.model.User;
import com.shchuko.corporatemessenger.security.jwt.JWTUserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author shchuko
 */
@Service
public class JWTUserDetailsService implements UserDetailsService {
    private UserService userService;

    public JWTUserDetailsService() {
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);

        if (user == null || !user.getStatus().equals(EntityStatus.ACTIVE)) {
            throw new UsernameNotFoundException("User " + username + " not found or don't active");
        }

        return JWTUserFactory.create(user);
    }
}
