package com.gci.pickem.controller;

import com.gci.pickem.exception.InvalidUserAccessException;
import com.gci.pickem.exception.UserNotFoundException;
import com.gci.pickem.model.UserView;
import com.gci.pickem.service.mail.MailService;
import com.gci.pickem.service.picks.PickService;
import com.gci.pickem.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {

    private UserService userService;
    private MailService mailService;
    private PickService pickService;

    @Autowired
    UserController(
        UserService userService,
        MailService mailService,
        PickService pickService
    ) {
        this.userService = userService;
        this.mailService = mailService;
        this.pickService = pickService;
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

    @GetMapping("api/v1/user/sendEmail")
    @PreAuthorize("hasAuthority('USER')")
    public void sendEmail() {
        pickService.notifyUsersWithoutPicks();
//        mailService.sendEmail();
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
