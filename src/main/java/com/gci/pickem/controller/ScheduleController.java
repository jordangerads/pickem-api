package com.gci.pickem.controller;

import com.gci.pickem.exception.MissingRequiredDataException;
import com.gci.pickem.model.GamesList;
import com.gci.pickem.service.schedule.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduleController {

    private ScheduleService scheduleService;

    @Autowired
    ScheduleController(
        ScheduleService scheduleService
    ) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/api/v1/games/season/{year}/week/{weekNum}")
    public GamesList getGamesByWeekOfYear(@PathVariable("year") Integer year, @PathVariable("weekNum") Integer week) {
        if (year == null) {
            throw new MissingRequiredDataException("Year must be provided");
        }

        if (week == null) {
            throw new MissingRequiredDataException("Week must be provided");
        }

        return scheduleService.getGamesForSeasonAndWeek(year, week);
    }
}
