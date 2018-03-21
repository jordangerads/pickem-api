package com.gci.pickem.service.game;

import com.gci.pickem.data.Game;
import com.gci.pickem.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class GamesServiceImpl implements GamesService {

    private GameRepository gameRepository;

    @Autowired
    GamesServiceImpl(
        GameRepository gameRepository
    ) {
        this.gameRepository = gameRepository;
    }

    @Override
    @Transactional
    public Game findByExternalId(Integer externalId) {
       return gameRepository.findByExternalId(externalId);
    }

    @Override
    @Transactional
    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    @Override
    public Collection<Game> findAllBySeasonAndWeek(int season, int week) {
        return gameRepository.findAllBySeasonAndWeek(season, week);
    }

    @Override
    public Game findGameAndFetchTeams(Long gameId) {
        return gameRepository.findGameAndFetchTeams(gameId);
    }
}
