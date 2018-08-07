package com.gci.pickem.repository;

import com.gci.pickem.data.PoolInvite;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface PoolInviteRepository extends CrudRepository<PoolInvite, Long> {

    Collection<PoolInvite> findByPoolId(long poolId);

    @Query(
        "SELECT pi " +
        "FROM PoolInvite pi " +
        "JOIN FETCH pi.invitedUser u " +
        "WHERE u.userId = :userId AND pi.poolId = :poolId")
    Optional<PoolInvite> findByUserIdAndPoolId(@Param("userId") long userId, @Param("poolId") long poolId);

    @Query(
        "SELECT pi " +
        "FROM PoolInvite pi " +
        "JOIN FETCH pi.invitedUser u " +
        "WHERE u.userId = :userId")
    Collection<PoolInvite> findByUserId(@Param("userId") long userId);
}
