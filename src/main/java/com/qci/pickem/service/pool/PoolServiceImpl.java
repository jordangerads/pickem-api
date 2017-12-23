package com.qci.pickem.service.pool;

import com.qci.pickem.data.Pool;
import com.qci.pickem.data.User;
import com.qci.pickem.data.UserPool;
import com.qci.pickem.exception.UserNotFoundException;
import com.qci.pickem.model.PoolView;
import com.qci.pickem.model.UserPoolRole;
import com.qci.pickem.repository.PoolRepository;
import com.qci.pickem.repository.UserPoolRepository;
import com.qci.pickem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PoolServiceImpl implements PoolService {

    private PoolRepository poolRepository;
    private UserPoolRepository userPoolRepository;
    private UserRepository userRepository;


    @Autowired
    PoolServiceImpl(
       PoolRepository poolRepository,
       UserPoolRepository userPoolRepository,
       UserRepository userRepository
    ) {
        this.poolRepository = poolRepository;
        this.userPoolRepository = userPoolRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public PoolView createPool(long userId, PoolView poolView) {
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new UserNotFoundException(String.format("No user exists with ID %d", userId));
        }

        // TODO: Validate that the user creating the pool has "rights" to create a pool. Paying member, etc.

        // Create the pool.
        Pool pool = poolRepository.save(new Pool(poolView));

        // Associate the creating user with the pool.
        UserPool userPool = new UserPool();
        userPool.setUserId(userId);
        userPool.setPoolId(pool.getPoolId());
        userPool.setUserRole(UserPoolRole.ADMIN.name());

        userPoolRepository.save(userPool);

        return new PoolView(pool);
    }
}
