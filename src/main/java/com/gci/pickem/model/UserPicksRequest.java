package com.gci.pickem.model;

import java.util.ArrayList;
import java.util.List;

public class UserPicksRequest {

    // This should probably go away as soon as we can get the user ID from the authentication object.
    private Long userId;
    private Long poolId;

    private List<GamePick> gamePicks = new ArrayList<>();

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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
