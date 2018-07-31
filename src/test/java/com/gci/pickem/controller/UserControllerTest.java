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
        toCreate.setPassword("$2a$10$blhN4BgQg2PcHYyPCYk1MOvs6gg8BK5/Pqn.Yp.UA9T3fDPetJ2Re");

        user = userRepository.save(toCreate);

        UserRole userRole = new UserRole();
        userRole.setUserId(user.getUserId());
        userRole.setRole("USER");

        userRole = userRoleRepository.save(userRole);

        user.setUserRoles(Sets.newHashSet(userRole));

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("test@test.com", "p@ssw0rd");
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

        UserView expected = new UserView(user);

        assertEquals(expected.getFirstName(), retrieved.getFirstName());
        assertEquals(expected.getLastName(), retrieved.getLastName());
        assertEquals(expected.getUsername(), retrieved.getUsername());
        assertEquals(expected.getPassword(), retrieved.getPassword());
        assertEquals(expected.getId(), retrieved.getId());
    }
}