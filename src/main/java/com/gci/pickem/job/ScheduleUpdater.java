package com.gci.pickem.job;

import com.gci.pickem.service.schedule.ScheduleService;
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

    @Autowired
    ScheduleUpdater(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // Run just after midnight of every day, eastern time.
    @Scheduled(cron = "0 5 0 * * *", zone = "America/New_York")
    public void updateFutureSchedules() {
        log.info("Executing schedule updates process.");

        // Look for all games in the next 7 days.
        scheduleService.processExternalGamesForNextDays(7);

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
