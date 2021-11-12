package com.shchuko.corporatemessenger.service.impl;

import com.shchuko.corporatemessenger.model.EntityStatus;
import com.shchuko.corporatemessenger.model.RoleTypes;
import com.shchuko.corporatemessenger.model.User;
import com.shchuko.corporatemessenger.repository.SystemRoleRepository;
import com.shchuko.corporatemessenger.repository.UserRepository;
import com.shchuko.corporatemessenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author shchuko
 */
@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private SystemRoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRoleRepository(SystemRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register new user with default role {@link RoleTypes#ROLE_USER}
     * @param user User with already existing username ({@link User#setUsername(String)})
     *             and NOT-hashed password ({@link User#setPasswordHash(String)})
     * @return Registered user stored into DB
     */
    @Override
    public User register(User user) {
        return register(user, RoleTypes.ROLE_USER);
    }

    /**
     * Register new user with specific roles
     * @param user User with already existing username ({@link User#setUsername(String)})
     *             and NOT-hashed password ({@link User#setPasswordHash(String)})
     * @param role Role to add
     * @param otherRoles Roles to add
     * @return Registered user stored into DB
     */
    @Override
    public User register(User user, RoleTypes role, RoleTypes... otherRoles) {
        Set<RoleTypes> userRoles = new HashSet<>();
        userRoles.add(role);
        userRoles.addAll(Arrays.asList(otherRoles));
        userRoles.add(RoleTypes.ROLE_USER);

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setRoles(userRoles.stream().map(roleType -> roleRepository.findByName(roleType)).collect(Collectors.toSet()));
        user.setStatus(EntityStatus.ACTIVE);

        return userRepository.save(user);
    }

    /**
     * Set new password for user
     *
     * @param user User to reset password
     * @param oldPassword Old password
     * @param newPassword New password
     * @return User with updated password
     */
    public boolean updatePassword(User user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            return false;
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    /**
     * Find user by username
     * @param username Username
     * @return User if found, null if not
     */
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Find user by id
     * @param id User id
     * @return User if found, null if not
     */
    @Override
    public User findById(long id) {
        return userRepository.findById(id);
    }

}
