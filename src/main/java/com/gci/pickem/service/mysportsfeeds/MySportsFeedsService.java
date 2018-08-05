package com.gci.pickem.service.mysportsfeeds;

import com.gci.pickem.model.mysportsfeeds.FullGameSchedule;
import com.gci.pickem.model.mysportsfeeds.GameScore;
import com.gci.pickem.model.mysportsfeeds.Scoreboard;

import java.time.Instant;
import java.util.Date;

public interface MySportsFeedsService {

    FullGameSchedule getGamesForSeasonAndWeek(int season, int week);

    FullGameSchedule getGamesUntilDaysFromNow(int days);

    Scoreboard getFinalGameScores(Instant date);

    GameScore getGameScore(Instant date, Integer msfGameId);
}
