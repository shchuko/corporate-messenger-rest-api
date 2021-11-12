package com.shchuko.corporatemessenger.rest;

import com.shchuko.corporatemessenger.dto.UserDTO;
import com.shchuko.corporatemessenger.model.User;
import com.shchuko.corporatemessenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shchuko
 */
@RestController
@RequestMapping(value = "/api/v1/admin")
public class AdminRESTControllerV1 {
    private UserService userService;

    public AdminRESTControllerV1() {
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable(name = "id") long id) {
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
    }

    @GetMapping("user-logins/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable(name = "username") String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(new UserDTO(user));
    }
}
