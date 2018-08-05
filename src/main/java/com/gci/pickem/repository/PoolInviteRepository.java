package com.gci.pickem.repository;

import com.gci.pickem.data.PoolInvite;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface PoolInviteRepository extends CrudRepository<PoolInvite, Long> {

    Collection<PoolInvite> findByPoolId(long poolId);

    @Query(
        value =
            "SELECT pi.* " +
            "FROM pool_invites pi " +
            "JOIN users u ON LOWER(pi.invitee_email) = LOWER(u.email) " +
            "WHERE u.user_id = ?1 AND pi.pool_id = ?2",
        nativeQuery = true)
    Optional<PoolInvite> findByUserIdAndPoolId(long userId, long poolId);

    @Query(
        value =
            "SELECT pi.* " +
            "FROM pool_invites pi " +
            "JOIN users u ON LOWER(pi.invitee_email) = LOWER(u.email) " +
            "WHERE u.user_id = ?1",
        nativeQuery = true)
    Collection<PoolInvite> findByUserId(long userId);
}
