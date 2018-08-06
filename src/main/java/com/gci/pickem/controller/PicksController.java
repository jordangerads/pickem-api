package com.gci.pickem.controller;

import com.gci.pickem.model.GamePick;
import com.gci.pickem.model.UserPicksRequest;
import com.gci.pickem.model.UserView;
import com.gci.pickem.service.picks.PickService;
import com.gci.pickem.service.picks.PickSubmissionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.gci.pickem.util.RequestUtil.getRequestUser;

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

    @PostMapping("/api/v1/picks")
    @PreAuthorize("hasAuthority('USER')")
    public PickSubmissionResponse submitPicks(@RequestBody UserPicksRequest picksRequest, HttpServletRequest request) {
        UserView user = getRequestUser(request);
        return pickService.saveUserPicks(user.getId(), picksRequest);
    }

    @GetMapping("/api/v1/picks/pool/{id}/season/{season}/week/{week}")
    @PreAuthorize("hasAuthority('USER')")
    public List<GamePick> getPicks(
        @PathVariable("id") long poolId,
        @PathVariable("season") int season,
        @PathVariable("week") int week,
        HttpServletRequest request) {

        UserView userView = getRequestUser(request);
        return pickService.getUserPicks(userView.getId(), poolId, season, week);
    }

    @GetMapping("/api/v1/picks/values")
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
