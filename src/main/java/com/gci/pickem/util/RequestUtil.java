package com.gci.pickem.util;

import com.gci.pickem.exception.UserNotFoundException;
import com.gci.pickem.model.UserView;
import com.gci.pickem.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@Component
public class RequestUtil {

    private static RequestUtil instance;

    private UserService userService;

    @Autowired
    RequestUtil(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void registerInstance() {
        instance = this;
    }

    public static UserView getRequestUser(HttpServletRequest request) {
        UserView user = instance.userService.getUserByUsername(request.getUserPrincipal().getName());
        if (user == null) {
            throw new UserNotFoundException(
                    String.format("No user found with username %s", request.getUserPrincipal().getName()));
        }

        return user;
    }
}
