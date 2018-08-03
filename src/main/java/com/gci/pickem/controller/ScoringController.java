package com.gci.pickem.controller;

import com.gci.pickem.service.scoring.ScoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScoringController {

    private ScoringService scoringService;

    @Autowired
    ScoringController(
        ScoringService scoringService
    ) {
        this.scoringService = scoringService;
    }

    // At some point this will grab the currently-logged-in user to return the score!
    @GetMapping(value = "'api/v1/score")
    public Integer getScore(@RequestParam("season") Integer season,
                              @RequestParam("week") Integer week,
                              @RequestParam("userId") Long userId,
                              @RequestParam("poolId") Long poolId) {
        return scoringService.getScore(userId, poolId, season, week);
    }
}
