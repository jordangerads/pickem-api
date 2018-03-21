package com.gci.pickem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gci.pickem.model.mysportsfeeds.GameEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class Game {

    private long gameId;
    private TeamView homeTeam;
    private TeamView awayTeam;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime gameTime;

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public TeamView getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(TeamView homeTeam) {
        this.homeTeam = homeTeam;
    }

    public TeamView getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(TeamView awayTeam) {
        this.awayTeam = awayTeam;
    }

    public LocalDateTime getGameTime() {
        return gameTime;
    }

    public void setGameTime(LocalDateTime gameTime) {
        this.gameTime = gameTime;
    }
}
