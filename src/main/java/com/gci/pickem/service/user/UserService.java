package com.gci.pickem.service.user;

import com.gci.pickem.model.ForgotPasswordRequest;
import com.gci.pickem.model.UserConfirmationView;
import com.gci.pickem.model.UserCreationRequest;
import com.gci.pickem.model.UserView;

public interface UserService {

    UserView getUserById(Long id);

    UserView getUserByUsername(String username);

    void createUser(UserCreationRequest user);

    void confirmUser(UserConfirmationView confirmationView);

    void userForgotPassword(ForgotPasswordRequest request);
}
