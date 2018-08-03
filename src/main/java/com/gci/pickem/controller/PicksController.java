package com.gci.pickem.controller;

import com.gci.pickem.model.UserPicksRequest;
import com.gci.pickem.service.picks.PickService;
import com.gci.pickem.service.picks.PickSubmissionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class PicksController {
    private static final Logger log = LoggerFactory.getLogger(PicksController.class);

    private PickService pickService;

    @Autowired
    PicksController(
        PickService pickService
    ) {
        this.pickService = pickService;
    }

    @PostMapping(value = "/api/v1/picks")
    public PickSubmissionResponse submitPicks(@RequestBody UserPicksRequest request) {
        return pickService.saveUserPicks(request);
    }

    @GetMapping(value = "/api/v1/picks/values")
    public List<Integer> getPossibleConfidences(@RequestParam("poolId") Long poolId, @RequestParam("season") Integer season, @RequestParam("week") Integer week) {
        return pickService.getConfidenceValues(poolId, season, week);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleException(RuntimeException e, HttpServletResponse response) throws IOException {
        log.error("{}", e);
        response.getOutputStream().write(e.getMessage().getBytes());
    }
}
