package com.gci.pickem.job;

import com.gci.pickem.data.Game;
import com.gci.pickem.model.GamesList;
import com.gci.pickem.service.game.GamesService;
import com.gci.pickem.service.schedule.ScheduleService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ScheduleUpdater {
    private static final Logger log = LoggerFactory.getLogger(ScheduleUpdater.class);

    private ScheduleService scheduleService;
    private GamesService gamesService;

    @Autowired
    ScheduleUpdater(ScheduleService scheduleService, GamesService gamesService) {
        this.scheduleService = scheduleService;
        this.gamesService = gamesService;
    }

    // Run just after midnight of every day, eastern time.
    @Scheduled(cron = "0 5 0 * * *", zone = "America/New_York")
    public void updateFutureSchedules() {
        log.info("Executing schedule updates process.");

        int updated = 0;

        // Look for all games in the next 7 days.
        GamesList gamesForWeek = scheduleService.getGamesForNextDays(7);
        if (CollectionUtils.isNotEmpty(gamesForWeek.getGames())) {
            for (com.gci.pickem.model.Game game : gamesForWeek.getGames()) {
                Game entity = gamesService.findById(game.getGameId());
                if (entity == null) {
                    // This is a major problem, we need to import this game since we don't have it.
                    log.error("This is a major problem!");
                    continue;
                }

                Long currentEpoch = entity.getGameTimeEpoch();
                Long incomingEpoch = game.getGameTimeEpoch();

                if (!currentEpoch.equals(incomingEpoch)) {
                    log.info(
                        "Game with ID {} has an updated epoch of {} (current stored value is {}). " +
                        "Updating to newly received time.", entity.getGameId(), incomingEpoch, currentEpoch);
                    updated++;
                }

                entity.setGameTimeEpoch(incomingEpoch);

                gamesService.saveGame(entity);
            }

            log.info("Updated {} game epochs", updated);
        } else {
            log.warn("No games found for the next seven days. This should be concerning if the season is in progress!");
        }

        log.info("Schedule process update complete.");
    }

    // Check for updated scores every 5 minutes of every day.
    @Scheduled(cron = "0 0/5 * * * *", zone = "America/New_York")
    public void updateScores() {
        log.info("Checking for updated scores");

        // Look back over the last day and find any scores for games that are finished.
        scheduleService.processScoresForDate(Instant.now());

        log.info("Done checking for updated scores");
    }
}
