package com.gci.pickem.controller;

import com.gci.pickem.model.UserView;
import com.gci.pickem.service.schedule.ScheduleService;
import com.gci.pickem.service.scoring.ScoringService;
import com.gci.pickem.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@RestController
public class ScoringController {
    private static final Logger log = LoggerFactory.getLogger(ScoringController.class);

    private ScoringService scoringService;
    private UserService userService;
    private ScheduleService scheduleService;

    @Autowired
    ScoringController(
        ScoringService scoringService,
        UserService userService,
        ScheduleService scheduleService
    ) {
        this.scoringService = scoringService;
        this.userService = userService;
        this.scheduleService = scheduleService;
    }

    // At some point this will grab the currently-logged-in user to return the score!
    @GetMapping("/api/v1/score")
    @PreAuthorize("hasAuthority('USER')")
    public Integer getScore(@RequestParam("season") Integer season,
                            @RequestParam("week") Integer week,
                            @RequestParam("poolId") Long poolId,
                            HttpServletRequest request) {
        String name = request.getUserPrincipal().getName();

        UserView user = userService.getUserByUsername(name);

        return scoringService.getScore(user.getId(), poolId, season, week);
    }

    @PostMapping("/api/v1/score/process")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void processGameScoresForDate(@RequestBody Map<String, Long> input) {
        Long epoch = input.get("epoch");
        if (epoch == null) {
            throw new RuntimeException("Missing required parameter 'epoch'.");
        }

        log.info("Processing scores for epoch {}", epoch);

        scheduleService.processScoresForDate(Instant.ofEpochMilli(epoch));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleException(RuntimeException e, HttpServletResponse response) throws IOException {
        log.error("Exception: {}", e.getMessage());
        response.getOutputStream().write(e.getMessage().getBytes());
    }
}
