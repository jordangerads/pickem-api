package com.gci.pickem.service.game;

import com.gci.pickem.data.Game;
import com.gci.pickem.model.mysportsfeeds.GameEntry;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface GamesService {

    Game findByExternalId(Integer externalId);

    Game saveGame(Game game);

    Game findById(Long id);

    Collection<Game> findAllBySeasonAndWeek(int season, int week);

    Game findGameAndFetchTeams(Long gameId);
}
