package com.gci.pickem.service.pool;

import com.gci.pickem.data.Pool;
import com.gci.pickem.data.User;
import com.gci.pickem.exception.UserNotFoundException;
import com.gci.pickem.model.PoolView;
import com.gci.pickem.repository.UserPoolRepository;
import com.gci.pickem.repository.UserRepository;
import com.gci.pickem.data.UserPool;
import com.gci.pickem.model.UserPoolRole;
import com.gci.pickem.repository.PoolRepository;
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
