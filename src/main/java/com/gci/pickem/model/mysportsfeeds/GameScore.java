package com.gci.pickem.model.mysportsfeeds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameScore {

    private GameEntry game;

    private String isUnplayed;
    private String isInProgress;
    private String isCompleted;

    private Integer homeScore;
    private Integer awayScore;

    public GameEntry getGame() {
        return game;
    }

    public void setGame(GameEntry game) {
        this.game = game;
    }

    public String getIsUnplayed() {
        return isUnplayed;
    }

    public void setIsUnplayed(String isUnplayed) {
        this.isUnplayed = isUnplayed;
    }

    public String getIsInProgress() {
        return isInProgress;
    }

    public void setIsInProgress(String isInProgress) {
        this.isInProgress = isInProgress;
    }

    public String getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(String isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }
}
