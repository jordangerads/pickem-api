package com.gci.pickem.controller;

import com.gci.pickem.model.UserView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    @Autowired private UserController controller;

    @Test
    public void testCreateUser() {
        UserView userView = new UserView();
        userView.setFirstName("Jordan");
        userView.setLastName("Test");

        UserView created = controller.createUser(userView);

        assertNotNull(created);

        UserView retrieved = controller.getUserById(created.getId());

        assertEquals(retrieved, created);
    }

}