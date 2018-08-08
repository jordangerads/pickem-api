package com.gci.pickem.util;

import com.gci.pickem.model.UserView;

public class PickemUserContext {

    private static final ThreadLocal<UserView> USER_VIEW_THREAD_LOCAL = new ThreadLocal<>();

    public static UserView getUser() {
        return USER_VIEW_THREAD_LOCAL.get();
    }

    public static void setUser(UserView user) {
        USER_VIEW_THREAD_LOCAL.set(user);
    }

    public static long getUserId() {
        UserView userView = USER_VIEW_THREAD_LOCAL.get();
        if (userView == null || userView.getId() == null) {
            throw new RuntimeException("No user ID found in the user context.");
        }

        return userView.getId();
    }

    public static void clear() {
        USER_VIEW_THREAD_LOCAL.remove();
    }
}