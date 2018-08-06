package com.gci.pickem.model;

import java.util.ArrayList;
import java.util.List;

public class UserPicksRequest {

    private Long poolId;
    private List<GamePick> gamePicks = new ArrayList<>();

    public Long getPoolId() {
        return poolId;
    }

    public void setPoolId(Long poolId) {
        this.poolId = poolId;
    }

    public List<GamePick> getGamePicks() {
        return gamePicks;
    }

    public void setGamePicks(List<GamePick> gamePicks) {
        this.gamePicks = gamePicks;
    }
}
