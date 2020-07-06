package com.shchuko.corporatemessenger.rest;

import com.shchuko.corporatemessenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

}
