package com.gci.pickem.controller;

import com.gci.pickem.exception.InvalidUserAccessException;
import com.gci.pickem.exception.UserNotFoundException;
import com.gci.pickem.model.UserView;
import com.gci.pickem.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
    @PreAuthorize("hasAuthority('USER')")
    public UserView getUserById(@PathVariable("id") Long userId, HttpServletRequest request) throws Exception {
        UserView userView = userService.getUserById(userId);

        if (userView == null) {
            throw new UserNotFoundException();
        }

        String requestingUser = request.getUserPrincipal().getName();
        if (!userView.getUsername().equals(requestingUser)) {
            throw new InvalidUserAccessException(String.format("User is not authorized to request data for user with ID %d", userId));
        }

        return userView;
    }

    @ExceptionHandler(InvalidUserAccessException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public void handleInvalidUserAccess() {
        // Nothing to do.
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void handleUserNotFound() {
        // Nothing to do.
    }

    @PostMapping("api/v1/user")
    public UserView createUser(@RequestBody UserView userView) {
        return userService.createUser(userView);
    }
}
