package com.gci.pickem.service.pool;

import com.gci.pickem.data.Pool;
import com.gci.pickem.data.PoolInvite;
import com.gci.pickem.data.User;
import com.gci.pickem.data.UserPool;
import com.gci.pickem.exception.UserNotFoundException;
import com.gci.pickem.model.*;
import com.gci.pickem.repository.PoolInviteRepository;
import com.gci.pickem.repository.PoolRepository;
import com.gci.pickem.repository.UserPoolRepository;
import com.gci.pickem.repository.UserRepository;
import com.gci.pickem.service.mail.MailService;
import com.gci.pickem.service.mail.MailType;
import com.gci.pickem.service.mail.SendEmailRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PoolServiceImpl implements PoolService {
    private static final Logger log = LoggerFactory.getLogger(PoolServiceImpl.class);

    private PoolRepository poolRepository;
    private UserPoolRepository userPoolRepository;
    private UserRepository userRepository;
    private PoolInviteRepository poolInviteRepository;
    private MailService mailService;

    @Autowired
    PoolServiceImpl(
       PoolRepository poolRepository,
       UserPoolRepository userPoolRepository,
       UserRepository userRepository,
       PoolInviteRepository poolInviteRepository,
       MailService mailService
    ) {
        this.poolRepository = poolRepository;
        this.userPoolRepository = userPoolRepository;
        this.userRepository = userRepository;
        this.poolInviteRepository = poolInviteRepository;
        this.mailService = mailService;
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

    @Override
    public void processPoolInviteResponse(long userId, long poolId, String inviteAction) {
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new UserNotFoundException(String.format("No user found for user ID %d", userId));
        }

        Optional<UserPool> userPool = user.getUserPools().stream().filter(item -> item.getPoolId().equals(poolId)).findFirst();
        if (userPool.isPresent()) {
            throw new RuntimeException(String.format("User with ID %d already belongs to pool with ID %d", userId, poolId));
        }

        Optional<PoolInvite> result = poolInviteRepository.findByUserIdAndPoolId(userId, poolId);
        if (!result.isPresent()) {
            throw new RuntimeException(String.format("User with ID %d does not have an invite for pool with ID %d", userId, poolId));
        }

        PoolInvite invite = result.get();
        PoolInviteStatus currentStatus = PoolInviteStatus.valueOf(invite.getInviteStatus());
        if (currentStatus != PoolInviteStatus.PENDING) {
            throw new RuntimeException(String.format("Pool invite for user ID %d to pool with ID %d has already been responded to.", userId, poolId));
        }

        PoolInviteStatus newStatus = PoolInviteStatus.valueOf(inviteAction);
        if (newStatus == PoolInviteStatus.ACCEPTED) {
            UserPool newUserPool = new UserPool();
            newUserPool.setPoolId(poolId);
            newUserPool.setUserId(userId);

            // User is always a participant when accepting a pool invite.
            newUserPool.setUserRole(UserPoolRole.PARTICIPANT.name());

            userPoolRepository.save(newUserPool);
        }

        invite.setInviteStatus(newStatus.name());

        poolInviteRepository.save(invite);
    }

    @Override
    public List<PoolInviteView> getPoolInvitesForPool(long userId, long poolId) {
        // Only allow pool invites to be seen by pool admins.
        validateUserIsPoolAdmin(userId, poolId);

        return poolInviteRepository.findByPoolId(poolId).stream().map(PoolInviteView::new).collect(Collectors.toList());
    }

    @Override
    public List<PoolInviteView> getPoolInvitesForUser(long userId) {
        return poolInviteRepository.findByUserId(userId).stream().map(PoolInviteView::new).collect(Collectors.toList());
    }

    @Override
    public List<String> sendPoolInvites(long userId, long poolId, List<String> emails, String clientUrl) {
        validateUserIsPoolAdmin(userId, poolId);

        User inviter = userRepository.findOne(userId);
        Pool pool = poolRepository.findOne(poolId);

        List<String> errorEmails = new ArrayList<>();

        // User exists, belongs to the pool, and is an admin of the pool. Invites can be sent!
        for (String email : emails) {
            try {
                PoolInvite invite = new PoolInvite();
                invite.setInvitingUserId(userId);
                invite.setPoolId(poolId);
                invite.setInviteeEmail(email);

                poolInviteRepository.save(invite);

                SendEmailRequest request = new SendEmailRequest();
                request.setTemplateId(MailType.POOL_INVITE.getTemplateId());
                request.setRecipientEmail(email);

                request.addRequestData("inviterFirstName", inviter.getFirstName());
                request.addRequestData("inviterLastName", inviter.getLastName());
                request.addRequestData("poolName", pool.getPoolName());
                request.addRequestData("clientUrl", clientUrl);

                mailService.sendEmail(request);

                log.debug("Invite for pool ID {} sent to {}.", poolId, email);
            } catch (Exception e) {
                // Don't let this prevent other invites from being sent.
                errorEmails.add(email);

                log.trace("", e);
                log.error("Error occurred while attempting to send invite to pool ID {} to email {}: {}", poolId, email, e.getMessage());
            }
        }

        return errorEmails;
    }

    @Override
    public List<UserPoolView> getPoolsForUser(long userId) {
        Set<UserPool> userPools = userPoolRepository.findByUserId(userId);
        if (CollectionUtils.isEmpty(userPools)) {
            return new ArrayList<>();
        }

        return
            userPools.stream()
                .map(userPool -> {
                    UserPoolView userPoolView = new UserPoolView();

                    userPoolView.setPoolId(userPool.getPoolId());
                    userPoolView.setPoolName(userPool.getPool().getPoolName());
                    userPoolView.setUserRole(userPool.getUserRole());
                    userPoolView.setUserId(userId);

                    return userPoolView;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void sendPoolMessage(long userId, long poolId, String message) {
        validateUserIsPoolAdmin(userId, poolId);

        User admin = userRepository.findOne(userId);

        Set<User> poolUsers = userRepository.findAllByPoolId(poolId);

        List<SendEmailRequest> messageRequests = new ArrayList<>();
        poolUsers.forEach(user -> messageRequests.add(createPoolMessageRequest(user, admin, message)));

        mailService.sendEmails(messageRequests);
    }

    private SendEmailRequest createPoolMessageRequest(User recipient, User sender, String message) {
        SendEmailRequest request = new SendEmailRequest();

        request.setRecipientName(recipient.getFirstName());
        request.setRecipientEmail(recipient.getEmail());
        request.setTemplateId(MailType.ADMIN_POOL_MESSAGE.getTemplateId());

        request.addRequestData("message", message);
        request.addRequestData("adminFirstName", sender.getFirstName());
        request.addRequestData("adminLastName", sender.getLastName());

        return request;
    }

    private void validateUserIsPoolAdmin(long userId, long poolId) {
        Set<UserPool> userPools = userPoolRepository.findByUserId(userId);

        Optional<UserPool> result =
            userPools.stream()
                .filter(entry -> entry.getPoolId().equals(poolId))
                .findFirst();

        if (!result.isPresent()) {
            throw new RuntimeException(String.format("User with ID %d is not associated with pool with ID %d", userId, poolId));
        }

        UserPool userPool = result.get();
        if (!UserPoolRole.ADMIN.name().equals(userPool.getUserRole())) {
            throw new RuntimeException(String.format("User with ID %d is not an admin of pool with ID %d", userId, poolId));
        }
    }
}
