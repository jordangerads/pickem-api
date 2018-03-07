package com.qci.pickem.controller;

import com.qci.pickem.model.UserPicksRequest;
import com.qci.pickem.service.picks.PickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PicksController {

    private PickService pickService;

    @Autowired
    PicksController(
        PickService pickService
    ) {
        this.pickService = pickService;
    }

    @PostMapping(value = "api/v1/picks")
    public void submitPicks(@RequestBody UserPicksRequest request) {
        pickService.saveUserPicks(request);
    }
}
