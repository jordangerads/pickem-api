package com.gci.pickem.repository;

import com.gci.pickem.data.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query(
        value =
            "(select user_id, pool_id from user_pool) " +
            "except " +
            "(select user_id, pool_id " +
            "from picks " +
            "where game_id in (?1) and chosen_team_id is not null and confidence is not null " +
            "group by user_id, pool_id " +
            "having count(*) = ?2)",
        nativeQuery = true)
    Set<Object[]> getUsersWithMissingPicks(Collection<Long> gameIds, int numGames);
}
