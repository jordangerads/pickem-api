package com.gci.pickem.service.game;

import com.gci.pickem.data.Game;

import java.util.Collection;

public interface GamesService {

    Game findByExternalId(Integer externalId);

    Game saveGame(Game game);

    Collection<Game> findAllBySeasonAndWeek(int season, int week);
}
