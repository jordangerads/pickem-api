package com.gci.pickem.model;

public class PoolCreateRequest {

    private Long userId;
    private PoolView poolView;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PoolView getPoolView() {
        return poolView;
    }

    public void setPoolView(PoolView poolView) {
        this.poolView = poolView;
    }
}
