package com.gci.pickem.repository;

import com.gci.pickem.data.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepository extends CrudRepository<Game, Long> {

    List<Game> findAllBySeasonAndWeek(Integer season, Integer week);

    Game findByExternalId(Integer externalId);
}
