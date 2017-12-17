package com.qci.pickem.service;

import com.qci.pickem.model.UserView;

public interface UserService {

    UserView getUserById(Long id);

    UserView createUser(UserView user);
}
