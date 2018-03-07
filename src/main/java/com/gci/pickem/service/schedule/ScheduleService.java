package com.gci.pickem.service.schedule;

import com.gci.pickem.model.WeekGames;

public interface ScheduleService {

    WeekGames getGamesForSeasonAndWeek(int season, int week);
}
