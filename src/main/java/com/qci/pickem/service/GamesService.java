package com.qci.pickem.service;

import com.qci.pickem.model.WeekGames;

public interface GamesService {

    WeekGames getGamesForSeasonAndWeek(int season, int week);
}
