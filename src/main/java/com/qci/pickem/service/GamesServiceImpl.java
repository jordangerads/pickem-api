package com.qci.pickem.service;

import com.qci.pickem.model.Game;
import com.qci.pickem.model.WeekGames;
import com.qci.pickem.model.mysportsfeeds.FullGameSchedule;
import com.qci.pickem.service.mysportsfeeds.MySportsFeedsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GamesServiceImpl implements GamesService {

    private MySportsFeedsService mySportsFeedsService;

    @Autowired
    GamesServiceImpl(
        MySportsFeedsService mySportsFeedsService
    ) {
        this.mySportsFeedsService = mySportsFeedsService;
    }

    @Override
    public WeekGames getGamesForSeasonAndWeek(int season, int week) {
        FullGameSchedule schedule = mySportsFeedsService.getGamesForSeasonAndWeek(season, week);

        List<Game> games =
            schedule.getGameEntries().stream()
                .filter(entry -> entry.getWeek().equals(week))
                .map(Game::new)
                .sorted(Comparator.comparing(Game::getGameTime))
                .collect(Collectors.toList());

        return new WeekGames(games);
    }
}
