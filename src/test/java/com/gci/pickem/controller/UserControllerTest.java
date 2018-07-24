package com.gci.pickem.controller;

import com.gci.pickem.data.User;
import com.gci.pickem.data.UserRole;
import com.gci.pickem.model.UserView;
import com.gci.pickem.repository.UserRepository;
import com.gci.pickem.repository.UserRoleRepository;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    @Autowired private UserController controller;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private UserRoleRepository userRoleRepository;

    private User user = null;

    @Before
    public void setup() {
        User toCreate = new User();

        toCreate.setFirstName("test");
        toCreate.setLastName("test");
        toCreate.setEmail("test@test.com");
        toCreate.setPassword("ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae");

        user = userRepository.save(toCreate);

        UserRole userRole = new UserRole();
        userRole.setUserId(user.getUserId());
        userRole.setRole("USER");

        userRole = userRoleRepository.save(userRole);

        user.setUserRoles(Sets.newHashSet(userRole));

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("test@test.com", "test123");
        Authentication auth = authenticationManager.authenticate(authRequest);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);
    }

    @Test
    public void testCreateUser() throws Exception {
        assertNotNull(user);

        Principal principal = new UsernamePasswordAuthenticationToken("test@test.com", "");

        HttpServletRequest request = new MockHttpServletRequest();
        ((MockHttpServletRequest) request).setUserPrincipal(principal);

        UserView retrieved = controller.getUserById(user.getUserId(), request);

        assertEquals(retrieved, new UserView(user));
    }
}