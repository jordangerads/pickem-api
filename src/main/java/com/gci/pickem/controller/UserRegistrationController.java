package com.gci.pickem.controller;

import com.gci.pickem.model.UserView;
import com.gci.pickem.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRegistrationController {
    private static final Logger log = LoggerFactory.getLogger(UserRegistrationController.class);

    private UserService userService;

    @Autowired
    UserRegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/v1/register")
    public UserView registerUser(@RequestBody UserView userView) {
        return userService.createUser(userView);
    }
}