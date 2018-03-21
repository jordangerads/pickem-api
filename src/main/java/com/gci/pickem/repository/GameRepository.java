package com.gci.pickem.repository;

import com.gci.pickem.data.Game;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends CrudRepository<Game, Long> {

    List<Game> findAllBySeasonAndWeek(Integer season, Integer week);

    Game findByExternalId(Integer externalId);

    @Query(
        "SELECT g " +
        "FROM Game g " +
        "JOIN FETCH g.homeTeam h " +
        "JOIN FETCH g.awayTeam a " +
        "WHERE g.gameId = :gameId"
    )
    Game findGameAndFetchTeams(@Param("gameId") Long gameId);
}
