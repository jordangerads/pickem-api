package com.qci.pickem.controller;

import com.qci.pickem.model.UserView;
import com.qci.pickem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private UserService userService;

    @Autowired
    UserController(
        UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping("api/v1/user/{id}")
    public UserView getUserById(@PathVariable("id") Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("api/v1/user")
    public UserView createUser(UserView userView) {
        return userService.createUser(userView);
    }
}
