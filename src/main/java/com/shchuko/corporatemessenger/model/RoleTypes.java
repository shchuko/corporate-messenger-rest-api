package com.shchuko.corporatemessenger.model;

/**
 * @author shchuko
 */
public enum RoleTypes {
    ROLE_ADMIN("ADMIN"), ROLE_USER("USER");

    private final String shorten;

    RoleTypes(String shorten) {
        this.shorten = shorten;
    }

    public String getShorten() {
        return shorten;
    }
}
