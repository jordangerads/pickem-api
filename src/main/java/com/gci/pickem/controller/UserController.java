package com.gci.pickem.controller;

import com.gci.pickem.exception.InvalidUserAccessException;
import com.gci.pickem.exception.UserNotFoundException;
import com.gci.pickem.model.ForgotPasswordRequest;
import com.gci.pickem.model.UserConfirmationView;
import com.gci.pickem.model.UserCreationRequest;
import com.gci.pickem.model.UserView;
import com.gci.pickem.service.picks.PickService;
import com.gci.pickem.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private UserService userService;
    private PickService pickService;

    @Autowired
    UserController(
        UserService userService,
        PickService pickService
    ) {
        this.userService = userService;
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

    @PostMapping("api/v1/user/sendEmail")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void sendEmail(@RequestBody Map<String, Long> input) {
        Long epoch = input.get("epoch");
        if (epoch == null) {
            throw new RuntimeException("Required input 'epoch' is missing.");
        }

        pickService.notifyUsersWithoutPicks(LocalDate.from(Instant.ofEpochMilli(epoch).atZone(ZoneId.of("America/New_York"))));
    }

    @PostMapping("/api/v1/user/register")
    public void registerUser(@RequestBody UserCreationRequest request, @RequestParam(value = "poolCode", required = false) String poolCode) {
        if (StringUtils.isNotBlank(poolCode)) {
            log.info("Processing user registration request for pool with code {}", poolCode);
        }

        userService.createUser(request);
    }

    @PostMapping("/api/v1/user/confirm")
    public void confirmUser(@RequestBody UserConfirmationView confirmView) {
        userService.confirmUser(confirmView);
    }

    @PostMapping("/api/v1/user/forgot-password")
    public void forgotPassword(@RequestBody ForgotPasswordRequest request) {
        // Consider how to prevent this from being spammed maliciously, as it sends an email each time!
        userService.userForgotPassword(request);
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

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void handleException(RuntimeException e, HttpServletResponse response) throws IOException {
        log.error("Exception: {}", e.getMessage());
        response.getOutputStream().write(e.getMessage().getBytes());
    }
}
