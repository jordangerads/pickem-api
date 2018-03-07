package com.gci.pickem.service.pool;

import com.gci.pickem.data.User;
import com.gci.pickem.exception.UserNotFoundException;
import com.gci.pickem.model.PoolView;
import com.gci.pickem.model.ScoringMethod;
import com.gci.pickem.model.UserPoolRole;
import com.gci.pickem.repository.UserPoolRepository;
import com.gci.pickem.repository.UserRepository;
import com.gci.pickem.data.UserPool;
import com.gci.pickem.repository.PoolRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PoolServiceImplTest {

    @Autowired private PoolService poolService;

    @Autowired private UserRepository userRepository;
    @Autowired private UserPoolRepository userPoolRepository;
    @Autowired private PoolRepository poolRepository;

    @Test(expected = UserNotFoundException.class)
    public void testCreatePoolForNonExistentUser() {
        poolService.createPool(-1L, new PoolView());
    }

    @Test
    public void testCreatePool() {
        String expectedName = "Test Pool Name";
        ScoringMethod scoringMethod = ScoringMethod.ABSOLUTE;

        User user = new User();
        user.setFirstName("Jordan");
        user.setLastName("Test");

        user = userRepository.save(user);

        PoolView poolView = new PoolView();
        poolView.setName(expectedName);
        poolView.setScoringMethod(scoringMethod.getId());

        PoolView actual = poolService.createPool(user.getUserId(), poolView);

        assertEquals(expectedName, actual.getName());
        assertEquals((long) ScoringMethod.ABSOLUTE.getId(), (long) actual.getScoringMethod());

        Set<UserPool> userPools = userPoolRepository.findByUserId(user.getUserId());

        assertEquals(1, userPools.size());

        UserPool userPool = userPools.iterator().next();
        assertEquals(user.getUserId(), userPool.getUserId());
        assertEquals(UserPoolRole.ADMIN.name(), userPool.getUserRole());
    }
}