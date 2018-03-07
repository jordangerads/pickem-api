package com.gci.pickem.controller;

import com.gci.pickem.model.UserView;
import com.gci.pickem.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public UserView createUser(@RequestBody UserView userView) {
        return userService.createUser(userView);
    }
}
