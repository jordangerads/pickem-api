package com.gci.pickem.service.user;

import com.gci.pickem.data.User;
import com.gci.pickem.data.UserRole;
import com.gci.pickem.exception.MissingRequiredDataException;
import com.gci.pickem.exception.UserAlreadyExistsException;
import com.gci.pickem.exception.UserNotFoundException;
import com.gci.pickem.model.ForgotPasswordRequest;
import com.gci.pickem.model.UserConfirmationView;
import com.gci.pickem.model.UserCreationRequest;
import com.gci.pickem.model.UserView;
import com.gci.pickem.repository.UserRepository;
import com.gci.pickem.repository.UserRoleRepository;
import com.gci.pickem.service.mail.MailService;
import com.gci.pickem.service.mail.MailType;
import com.gci.pickem.service.mail.SendEmailRequest;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private PasswordEncoder passwordEncoder;
    private MailService mailService;

    @Autowired
    UserServiceImpl(
        UserRepository userRepository,
        UserRoleRepository userRoleRepository,
        PasswordEncoder passwordEncoder,
        MailService mailService
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    @Override
    public UserView getUserById(Long id) {
        User user = userRepository.findOne(id);
        return user != null ? new UserView(user) : null;
    }

    @Override
    public UserView getUserByUsername(String username) {
        Optional<User> user = userRepository.findByEmail(username);
        return user.map(UserView::new).orElse(null);
    }

    @Override
    @Transactional
    public void createUser(UserCreationRequest request) {
        if (!userCreateRequestIsValid(request)) {
            String msg = "User registration requests require a first name, last name, and username";
            log.warn(msg);
            throw new MissingRequiredDataException(msg);
        }

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            String msg = String.format("User with email %s already exists.", request.getEmail());
            log.warn(msg);
            throw new UserAlreadyExistsException(msg);
        }

        // User data is present, no already existing user, let's create!
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        // Generate the nonce for the new user.
        String nonce = generateNonce();
        user.setRegistrationCode(nonce);

        user = userRepository.save(user);

        // Assign the user the USER role!
        UserRole role = new UserRole();
        role.setUserId(user.getUserId());
        role.setRole("USER");

        role = userRoleRepository.save(role);

        user.setUserRoles(Sets.newHashSet(role));

        // New user was created successfully. Send an email so the user can set the password.
        sendUserNonceEmail(user, nonce, MailType.USER_REGISTRATION, request.getClientUrl());
    }

    private String generateNonce() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    @Override
    @Transactional
    public void userForgotPassword(ForgotPasswordRequest request) {
        Optional<User> result = userRepository.findByEmail(request.getUserEmail());
        if (!result.isPresent()) {
            String msg = String.format("No user found with email %s", request.getUserEmail());
            log.warn(msg);
            throw new UserNotFoundException(msg);
        }

        // Update the user's nonce and send the forgot password email.
        User user = result.get();

        String nonce = generateNonce();

        user.setRegistrationCode(nonce);
        user = userRepository.save(user);

        sendUserNonceEmail(user, nonce, MailType.FORGOT_PASSWORD, request.getClientUrl());
    }

    private void sendUserNonceEmail(User user, String nonce, MailType mailType, String clientUrl) {
        SendEmailRequest request = new SendEmailRequest();
        request.setTemplateId(mailType.getTemplateId());
        request.setRecipientEmail(user.getEmail());
        request.setRecipientName(user.getFirstName());

        Map<String, Object> requestData =
            ImmutableMap.of(
                "nonce", nonce,
                "firstName", user.getFirstName(),
                "clientUrl", clientUrl);

        request.setRequestData(requestData);

        mailService.sendEmail(request);
    }

    @Override
    public void confirmUser(UserConfirmationView confirmationView) {
        Optional<User> result = userRepository.findByRegistrationCode(confirmationView.getNonce());
        if (!result.isPresent()) {
            String msg = "No user found for the provided registration code.";
            log.warn(msg);
            throw new UserNotFoundException(msg);
        }

        User user = result.get();

        user.setPassword(passwordEncoder.encode(confirmationView.getPassword()));
        user.setActive(true);

        userRepository.save(user);
    }

    private boolean userCreateRequestIsValid(UserCreationRequest user) {
        return
            StringUtils.isNotBlank(user.getFirstName()) &&
            StringUtils.isNotBlank(user.getLastName()) &&
            StringUtils.isNotBlank(user.getEmail());
    }
}
