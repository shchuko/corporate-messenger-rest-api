package com.shchuko.corporatemessenger.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shchuko.corporatemessenger.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shchuko
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private long id;
    private String login;
    private List<String> roles;

    public UserDTO(User user) {
        this.id = user.getId();
        this.login = user.getUsername();
        this.roles = user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toList());
    }


}
