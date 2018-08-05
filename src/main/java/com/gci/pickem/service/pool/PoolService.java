package com.gci.pickem.service.pool;

import com.gci.pickem.model.PoolInviteView;
import com.gci.pickem.model.PoolView;

import java.util.List;

public interface PoolService {

    PoolView createPool(long userId, PoolView poolView);

    // Returns email addresses that had failures.
    List<String> sendPoolInvites(long userId, long poolId, List<String> emails, String clientUrl);

    void processPoolInviteResponse(long userId, long poolId, String inviteAction);

    List<PoolInviteView> getPoolInvitesForPool(long userId, long poolId);

    List<PoolInviteView> getPoolInvitesForUser(long userId);
}
