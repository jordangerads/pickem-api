package com.gci.pickem.controller;

import com.gci.pickem.model.*;
import com.gci.pickem.service.pool.PoolService;
import com.gci.pickem.util.PickemUserContext;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Map;

@RestController
public class PoolController {
    private static final Logger log = LoggerFactory.getLogger(PoolController.class);

    private PoolService poolService;

    @Autowired
    PoolController(
        PoolService poolService
    ) {
        this.poolService = poolService;
    }

    // In the future, we will be able to get the creator of the pool and stuff from the authentication object.
    @PostMapping("/api/v1/pool")
    @PreAuthorize("hasAuthority('USER')")
    public PoolView createPool(@RequestBody PoolCreateRequest createRequest) {
        return poolService.createPool(createRequest.getUserId(), createRequest.getPoolView());
    }

    @PostMapping("/api/v1/pool/{id}/invite/respond")
    @PreAuthorize("hasAuthority('USER')")
    public void processPoolInviteResponse(@PathVariable("id") Long poolId, @RequestBody Map<String, Object> input, HttpServletRequest request) {
        String inviteAction = input.get("inviteAction").toString();
        if (StringUtils.isBlank(inviteAction)) {
            throw new RuntimeException("Request is missing required inviteAction parameter");
        }

        poolService.processPoolInviteResponse(PickemUserContext.getUserId(), poolId, inviteAction);
    }

    @GetMapping("/api/v1/pool/{id}/invite")
    @PreAuthorize("hasAuthority('USER')")
    public List<PoolInviteView> getPoolInvitesForPool(@PathVariable("id") Long poolId, HttpServletRequest request) {
        return poolService.getPoolInvitesForPool(PickemUserContext.getUserId(), poolId);
    }

    @GetMapping("/api/v1/pool/list")
    public List<UserPoolView> getUserPools(HttpServletRequest request) {
        return poolService.getPoolsForUser(PickemUserContext.getUserId());
    }

    @GetMapping("/api/v1/pool/invite")
    @PreAuthorize("hasAuthority('USER')")
    public List<PoolInviteView> getPoolInvitesForUser(HttpServletRequest request) {
        return poolService.getPoolInvitesForUser(PickemUserContext.getUserId());
    }

    @PostMapping("/api/v1/pool/invite")
    @PreAuthorize("hasAuthority('USER')")
    public PoolInviteResponse sendPoolInvite(@RequestBody PoolInviteRequest inviteRequest, HttpServletRequest request) {
        return new PoolInviteResponse(
            poolService.sendPoolInvites(
                PickemUserContext.getUserId(),
                inviteRequest.getPoolId(),
                inviteRequest.getInviteeEmails(),
                inviteRequest.getClientUrl()));
    }

    @PostMapping("/api/v1/pool/{id}/message")
    @PreAuthorize("hasAuthority('USER')")
    public void sendPoolMessage(@PathVariable("id") Long poolId, @RequestBody PoolMessageRequest messageRequest, HttpServletRequest request) {
        poolService.sendPoolMessage(PickemUserContext.getUserId(), poolId, messageRequest.getMessage());
    }


    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleException(RuntimeException e, HttpServletResponse response) throws IOException {
        log.error("Error occurred during request: {}", e.getMessage());
        response.getOutputStream().write(e.getMessage().getBytes());
    }
}
