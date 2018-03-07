package com.gci.pickem.model.mysportsfeeds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Scoreboard {

    @JsonProperty("gameScore")
    private List<GameScore> gameScores;

    public List<GameScore> getGameScores() {
        return gameScores;
    }

    public void setGameScores(List<GameScore> gameScores) {
        this.gameScores = gameScores;
    }
}
