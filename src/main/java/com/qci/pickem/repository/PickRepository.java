package com.qci.pickem.repository;

import com.qci.pickem.data.Pick;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface PickRepository extends CrudRepository<Pick, Long> {

    @Query(
        "SELECT p " +
        "FROM Pick p " +
        "JOIN FETCH p.game g " +
        "WHERE p.userId = :userId AND " +
        "   p.poolId = :poolId AND " +
        "   g.season = :season AND " +
        "   g.week = :week")
    Set<Pick> getPicks(@Param("userId") long userId,
                       @Param("poolId") long poolId,
                       @Param("season") int season,
                       @Param("week") int week);

    @Query(
            "SELECT p " +
                    "FROM Pick p " +
                    "JOIN FETCH p.game g " +
                    "WHERE p.userId = :userId AND " +
                    "   p.poolId = :poolId")
    Set<Pick> getPicks(@Param("userId") long userId,
                       @Param("poolId") long poolId);
}
