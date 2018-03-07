package com.gci.pickem.service.user;

import com.gci.pickem.model.UserView;

public interface UserService {

    UserView getUserById(Long id);

    UserView createUser(UserView user);
}
