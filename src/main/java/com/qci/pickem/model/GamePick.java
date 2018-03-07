package com.qci.pickem.model;

/**
 * Represents a single game pick for a user.
 */
public class GamePick {

    private Long gameId;
    private Long chosenTeamId;
    private Integer confidence;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getChosenTeamId() {
        return chosenTeamId;
    }

    public void setChosenTeamId(Long chosenTeamId) {
        this.chosenTeamId = chosenTeamId;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }
}
