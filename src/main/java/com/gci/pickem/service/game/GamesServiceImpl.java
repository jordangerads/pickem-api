package com.gci.pickem.service.game;

import com.gci.pickem.data.Game;
import com.gci.pickem.model.mysportsfeeds.FullGameSchedule;
import com.gci.pickem.model.mysportsfeeds.GameScore;
import com.gci.pickem.model.mysportsfeeds.Team;
import com.gci.pickem.repository.GameRepository;
import com.gci.pickem.model.WeekGames;
import com.gci.pickem.model.mysportsfeeds.GameEntry;
import com.gci.pickem.service.mysportsfeeds.MySportsFeedsService;
import com.gci.pickem.service.team.TeamService;
import com.gci.pickem.util.SeasonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public Game findByExternalId(Integer externalId) {
       return gameRepository.findByExternalId(externalId);
    }

    @Override
    @Transactional
    public void saveGame(Game game) {
        gameRepository.save(game);
    }

    @Override
    public Collection<Game> findAllBySeasonAndWeek(int season, int week) {
        return gameRepository.findAllBySeasonAndWeek(season, week);
    }
}
