package com.qci.pickem.service.mysportsfeeds;

import com.qci.pickem.model.mysportsfeeds.FullGameSchedule;

public interface MySportsFeedsService {

    FullGameSchedule getGamesForSeasonAndWeek(int season, int week);
}
