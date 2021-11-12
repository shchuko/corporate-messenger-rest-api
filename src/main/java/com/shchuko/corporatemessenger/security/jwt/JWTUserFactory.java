package com.shchuko.corporatemessenger.security.jwt;

import com.shchuko.corporatemessenger.model.EntityStatus;
import com.shchuko.corporatemessenger.model.Role;
import com.shchuko.corporatemessenger.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shchuko
 */
public final class JWTUserFactory {
    public JWTUserFactory() {
    }

    /**
     * Create {@link JWTUser} from User object
     * @param user User
     * @return Created {@link JWTUser}
     */
    public static JWTUser create(User user) {
        return new JWTUser(user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getStatus().equals(EntityStatus.ACTIVE),
                mapToGrantedAuthority(user.getRoles()));
    }

    private static List<GrantedAuthority> mapToGrantedAuthority(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());
    }
}
