package com.gci.pickem.service.pool

import com.gci.pickem.data.Pool
import com.gci.pickem.data.PoolInvite
import com.gci.pickem.data.User
import com.gci.pickem.data.UserPool
import com.gci.pickem.exception.UserNotFoundException
import com.gci.pickem.model.PoolView
import com.gci.pickem.repository.PoolInviteRepository
import com.gci.pickem.repository.PoolRepository
import com.gci.pickem.repository.UserPoolRepository
import com.gci.pickem.repository.UserRepository
import com.gci.pickem.service.mail.MailService
import com.gci.pickem.service.mail.SendEmailRequest
import com.google.common.collect.Lists
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

    def """Exception should be thrown if user attempts to accept an invite for a pool
           he/she doesn't have a standing invitation for""" () {
        setup:
            userRepository.findOne(1L) >> new User(userPools: Lists.newArrayList(new UserPool(poolId: 2L)))
            poolInviteRepository.findByUserIdAndPoolId(1L, 1L) >> Optional.empty()

        when:
            poolService.processPoolInviteResponse(1L, 1L, "ACCEPTED")

        then:
            RuntimeException ex = thrown()

            ex.message == 'User with ID 1 does not have an invite for pool with ID 1'
    }

    def """Exception should be thrown if user attempts to accept an invite that has
           already been accepted or rejected""" () {
        setup:
            userRepository.findOne(1L) >> new User(userPools: Lists.newArrayList(new UserPool(poolId: 2L)))
            poolInviteRepository.findByUserIdAndPoolId(1L, 1L) >> Optional.of(new PoolInvite(inviteStatus: 'REJECTED'))

        when:
            poolService.processPoolInviteResponse(1L, 1L, "ACCEPTED")

        then:
            RuntimeException ex = thrown()

            ex.message == 'Pool invite for user ID 1 to pool with ID 1 has already been responded to.'
    }

    def """processPoolInviteResponse: Happy path""" () {
        setup:
            UserPool savedUserPool = null
            PoolInvite savedPoolInvite = null

            userRepository.findOne(1L) >> new User(userPools: Lists.newArrayList(new UserPool(poolId: 2L)))
            poolInviteRepository.findByUserIdAndPoolId(1L, 1L) >> Optional.of(new PoolInvite(inviteStatus: 'PENDING'))
            userPoolRepository.save(_ as UserPool) >> { UserPool userPool ->
                savedUserPool = userPool
            }
            poolInviteRepository.save(_ as PoolInvite) >> { PoolInvite poolInvite ->
                savedPoolInvite = poolInvite
            }

        when:
            poolService.processPoolInviteResponse(1L, 1L, "ACCEPTED")

        then:
            savedUserPool != null
            savedUserPool.poolId == 1L
            savedUserPool.userId == 1L
            savedUserPool.userRole == 'PARTICIPANT'

            savedPoolInvite != null
            savedPoolInvite.inviteStatus == 'ACCEPTED'
    }

    def "Exception should be thrown if user can't be found for create pool request" () {
        setup:
            userRepository.findOne(1L) >> null

        when:
            poolService.createPool(1L, new PoolView(name: "Test Pool", scoringMethod: 1))

        then:
            UserNotFoundException ex = thrown()

            ex.message == 'No user exists with ID 1'
    }

    def "createPool: Happy path" () {
        setup:
            Pool savedPool = null
            UserPool savedUserPool = null

            userRepository.findOne(1L) >> new User(userId: 1L)
            poolRepository.save(_ as Pool) >> { Pool pool ->
                pool.poolId = 1L
                savedPool = pool
            }
            userPoolRepository.save(_ as UserPool) >> { UserPool userPool ->
                userPool.userPoolId = 1L
                savedUserPool = userPool
            }

        when:
            PoolView actual = poolService.createPool(1L, new PoolView(name: "Test Pool", scoringMethod: 1))

        then:
            actual != null
            actual.scoringMethod == 1
            actual.name == "Test Pool"

            savedPool != null
            savedPool.poolId == 1L
            savedPool.scoringMethod == 1
            savedPool.poolName == "Test Pool"

            savedUserPool != null
            savedUserPool.userPoolId == 1L
            savedUserPool.poolId == 1L
            savedUserPool.userId == 1L
            savedUserPool.userRole == 'ADMIN'
    }

    def "Exception should be thrown if user has no pools for validateUserIsPoolAdmin" () {
        given:
            userPoolRepository.findByUserId(1L) >> null

        when:
            poolService.validateUserIsPoolAdmin(1L, 1L)

        then:
            RuntimeException ex = thrown()
            ex.message == 'No pools found for user with ID 1'
    }

    def "Exception should be thrown if user is not associated with pool for validateUserIsPoolAdmin" () {
        given:
            userPoolRepository.findByUserId(1L) >> Lists.newArrayList(new UserPool(poolId: 2))

        when:
            poolService.validateUserIsPoolAdmin(1L, 1L)

        then:
            RuntimeException ex = thrown()
            ex.message == 'User with ID 1 is not associated with pool with ID 1'
    }

    def "Exception should be thrown if user is not admin for pool for validateUserIsPoolAdmin" () {
        given:
            userPoolRepository.findByUserId(1L) >> Lists.newArrayList(new UserPool(poolId: 1L, userRole: 'PARTICIPANT'))

        when:
            poolService.validateUserIsPoolAdmin(1L, 1L)

        then:
            RuntimeException ex = thrown()
            ex.message == 'User with ID 1 is not an admin of pool with ID 1'
    }

    def "validateUserIsPoolAdmin: Happy path" () {
        given:
            userPoolRepository.findByUserId(1L) >> Lists.newArrayList(new UserPool(poolId: 1L, userRole: 'ADMIN'))

        when:
            poolService.validateUserIsPoolAdmin(1L, 1L)

        then:
            noExceptionThrown()
    }

    def "sendPoolInvites: Happy path" () {
        given:
            // A bunch of mocks
            List<SendEmailRequest> emailRequests = new ArrayList<>()

            userPoolRepository.findByUserId(1L) >> Lists.newArrayList(new UserPool(poolId: 1L, userRole: 'ADMIN'))
            userRepository.findOne(1L) >> new User(userId: 1L, firstName: 'Test', lastName: 'Admin')
            poolRepository.findOne(1L) >> new Pool(poolId: 1L, poolName: 'Test Pool 1')
            mailService.sendEmail(_ as SendEmailRequest) >> { SendEmailRequest sendEmailRequest ->
                if (sendEmailRequest.recipientEmail == 'second@email.com') {
                    throw new RuntimeException('Silly exception')
                }

                emailRequests.add(sendEmailRequest)
            }

        when:
            List<String> response =
                poolService.sendPoolInvites(
                    1L,
                    1L,
                    Lists.newArrayList('first@email.com', 'second@email.com', 'third@email.com'),
                    'https://www.test.com')

        then:
            emailRequests.size() == 2

            SendEmailRequest first = emailRequests.get(0)
            first.recipientEmail == 'first@email.com'
            first.templateId == 'tem_jbHYWHKyJQmC3qbXpVyRkH3B'
            first.requestData.size() == 4
            first.requestData.get('inviterFirstName') == 'Test'
            first.requestData.get('inviterLastName') == 'Admin'
            first.requestData.get('poolName') == 'Test Pool 1'
            first.requestData.get('clientUrl') == 'https://www.test.com'

            SendEmailRequest second = emailRequests.get(1)
            second.recipientEmail == 'third@email.com'
            second.templateId == 'tem_jbHYWHKyJQmC3qbXpVyRkH3B'
            second.requestData.size() == 4
            second.requestData.get('inviterFirstName') == 'Test'
            second.requestData.get('inviterLastName') == 'Admin'
            second.requestData.get('poolName') == 'Test Pool 1'
            second.requestData.get('clientUrl') == 'https://www.test.com'

            response.size() == 1
            response.get(0) == 'second@email.com'
    }
}
