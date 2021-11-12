package com.shchuko.corporatemessenger.service;

import com.shchuko.corporatemessenger.model.RoleTypes;
import com.shchuko.corporatemessenger.model.User;

import java.util.List;

/**
 * @author shchuko
 */
public interface UserService {
    /**
     * Register new user with default role {@link RoleTypes#ROLE_USER}
     * @param user User with already existing username ({@link User#setUsername(String)})
     *             and NOT-hashed password ({@link User#setPasswordHash(String)})
     * @return Registered user stored into DB
     */
    User register(User user);

    /**
     * Register new user with specific roles
     * @param user User with already existing username ({@link User#setUsername(String)})
     *             and NOT-hashed password ({@link User#setPasswordHash(String)})
     * @param role Role to add
     * @param otherRoles Roles to add
     * @return Registered user stored into DB
     */
    User register(User user, RoleTypes role, RoleTypes... otherRoles);

    /**
     * Find user by username
     * @param username Username
     * @return User if found, null if not
     */
    User findByUsername(String username);

    /**
     * Find user by id
     * @param id User id
     * @return User if found, null if not
     */
    User findById(long id);
}
