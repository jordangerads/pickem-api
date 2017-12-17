package com.qci.pickem.controller;

import com.qci.pickem.exception.MissingRequiredDataException;
import com.qci.pickem.model.WeekGames;
import com.qci.pickem.service.GamesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GamesController {

    private GamesService gamesService;

    @Autowired
    GamesController(
        GamesService gamesService
    ) {
        this.gamesService = gamesService;
    }

    @GetMapping("api/v1/games/season/{year}/week/{weekNum}")
    public WeekGames getGamesByWeekOfYear(@PathVariable("year") Integer year, @PathVariable("weekNum") Integer week) {
        if (year == null) {
            throw new MissingRequiredDataException("Year must be provided");
        }

        if (week == null) {
            throw new MissingRequiredDataException("Week must be provided");
        }

        return gamesService.getGamesForSeasonAndWeek(year, week);
    }
}
