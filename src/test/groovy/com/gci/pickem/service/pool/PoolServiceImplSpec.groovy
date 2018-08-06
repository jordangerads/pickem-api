package com.gci.pickem.service.pool

import com.gci.pickem.data.User
import com.gci.pickem.data.UserPool
import com.gci.pickem.exception.UserNotFoundException
import com.gci.pickem.repository.PoolInviteRepository
import com.gci.pickem.repository.PoolRepository
import com.gci.pickem.repository.UserPoolRepository
import com.gci.pickem.repository.UserRepository
import com.gci.pickem.service.mail.MailService
import org.assertj.core.util.Lists
import spock.lang.Specification
import spock.lang.Subject

class PoolServiceImplSpec extends Specification {

    @Subject
    private PoolService poolService

    private PoolRepository poolRepository
    private UserPoolRepository userPoolRepository
    private UserRepository userRepository
    private PoolInviteRepository poolInviteRepository
    private MailService mailService

    def setup() {
        poolRepository = Mock(PoolRepository.class)
        userPoolRepository = Mock(UserPoolRepository.class)
        userRepository = Mock(UserRepository.class)
        poolInviteRepository = Mock(PoolInviteRepository.class)
        mailService = Mock(MailService.class)

        poolService = new PoolServiceImpl(poolRepository, userPoolRepository, userRepository, poolInviteRepository, mailService)
    }

    def """If no user is found in the database when processing pool invite responses then
           a user not found exception should be thrown""" () {
        setup: "no user found for user ID 1"
            userRepository.findOne(1L) >> null

        when:
            poolService.processPoolInviteResponse(1, 1, "ACCEPTED")

        then:
            UserNotFoundException ex = thrown()

            ex.message == 'No user found for user ID 1'
    }

    def """Exception should be thrown if user attempts to accept an invite for a pool
           he/she already belongs to""" () {
        setup:
            userRepository.findOne(1L) >> new User(userPools: Lists.newArrayList(new UserPool(poolId: 1L)))

        when:
            poolService.processPoolInviteResponse(1L, 1L, "ACCEPTED")

        then:
            RuntimeException ex = thrown()

            ex.message == 'User with ID 1 already belongs to pool with ID 1'
    }
}
