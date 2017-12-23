package com.qci.pickem.controller;

import com.qci.pickem.model.PoolCreateRequest;
import com.qci.pickem.model.PoolView;
import com.qci.pickem.service.pool.PoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PoolController {

    private PoolService poolService;

    @Autowired
    PoolController(
        PoolService poolService
    ) {
        this.poolService = poolService;
    }

    // In the future, we will be able to get the creator of the pool and stuff from the authentication object.
    @PostMapping("api/v1/pool")
    public PoolView createPool(@RequestBody PoolCreateRequest createRequest) {
        return poolService.createPool(createRequest.getUserId(), createRequest.getPoolView());
    }
}
