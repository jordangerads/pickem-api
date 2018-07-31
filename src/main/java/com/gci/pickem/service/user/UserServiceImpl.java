package com.gci.pickem.service.user;

import com.gci.pickem.data.User;
import com.gci.pickem.data.UserRole;
import com.gci.pickem.exception.MissingRequiredDataException;
import com.gci.pickem.exception.UserAlreadyExistsException;
import com.gci.pickem.exception.UserNotFoundException;
import com.gci.pickem.model.UserView;
import com.gci.pickem.repository.UserRepository;
import com.gci.pickem.repository.UserRoleRepository;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserServiceImpl(
        UserRepository userRepository,
        UserRoleRepository userRoleRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserView getUserById(Long id) {
        User user = userRepository.findOne(id);
        return user != null ? new UserView(user) : null;
    }

    @Override
    @Transactional
    public UserView createUser(UserView user) {
        if (!userCreateRequestIsValid(user)) {
            String msg = "User registration requests require a first name, last name, username, and password";
            log.warn(msg);
            throw new MissingRequiredDataException(msg);
        }

        Optional<User> existingUser = userRepository.findByEmail(user.getUsername());
        if (existingUser.isPresent()) {
            String msg = String.format("User with email %s already exists.", user.getUsername());
            log.warn(msg);
            throw new UserAlreadyExistsException(msg);
        }

        // User data is present, no already existing user, let's create!
        User newUser = new User(user);

        // Password was wr
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        newUser = userRepository.save(newUser);

        // Assign the user the USER role!
        UserRole role = new UserRole();
        role.setUserId(newUser.getUserId());
        role.setRole("USER");

        role = userRoleRepository.save(role);

        newUser.setUserRoles(Sets.newHashSet(role));

        // New user was created successfully. Send a welcome email!

        return new UserView(newUser);
    }

    private boolean userCreateRequestIsValid(UserView user) {
        return
            StringUtils.isNotBlank(user.getFirstName()) &&
            StringUtils.isNotBlank(user.getLastName()) &&
            StringUtils.isNotBlank(user.getUsername()) &&
            StringUtils.isNotBlank(user.getPassword());
    }
}
