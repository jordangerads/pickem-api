package com.gci.pickem.service.user;

import com.gci.pickem.data.User;
import com.gci.pickem.model.UserView;
import com.gci.pickem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    UserServiceImpl(
        UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public UserView getUserById(Long id) {
        User user = userRepository.findOne(id);
        return user != null ? new UserView(user) : null;
    }

    @Override
    public UserView createUser(UserView user) {
        return new UserView(userRepository.save(new User(user)));
    }
}
