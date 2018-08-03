package com.gci.pickem.service.schedule;

import com.gci.pickem.model.GamesList;

import java.time.Instant;

public interface ScheduleService {

    GamesList getGamesForSeasonAndWeek(int season, int week);

    GamesList getGamesForNextDays(int days);

    void processScoresForDate(Instant date);
}
