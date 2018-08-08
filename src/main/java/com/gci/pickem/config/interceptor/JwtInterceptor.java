package com.gci.pickem.config.interceptor;

import com.gci.pickem.model.UserView;
import com.gci.pickem.service.user.UserService;
import com.gci.pickem.util.PickemUserContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtInterceptor extends HandlerInterceptorAdapter {

    private UserService userService;

    public JwtInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        UserView user = userService.getUserByUsername(request.getUserPrincipal().getName());
        if (user != null) {
            PickemUserContext.setUser(user);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        PickemUserContext.clear();
    }
}
