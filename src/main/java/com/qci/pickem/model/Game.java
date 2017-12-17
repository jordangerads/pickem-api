package com.qci.pickem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qci.pickem.model.mysportsfeeds.GameEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Game {
    private final String homeTeam;
    private final String awayTeam;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime gameTime;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public Game(GameEntry gameEntry) {
        this.homeTeam = NflTeams.getTeamByCode(gameEntry.getHomeTeam().getAbbreviation()).getFullName();
        this.awayTeam = NflTeams.getTeamByCode(gameEntry.getAwayTeam().getAbbreviation()).getFullName();

        String date = DATE_FORMAT.format(gameEntry.getDate());
        String fullTime = String.format("%s %s", date, gameEntry.getTime());

        this.gameTime = LocalDateTime.parse(fullTime, DateTimeFormatter.ofPattern("yyyy-MM-dd h:mma"));
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public LocalDateTime getGameTime() {
        return gameTime;
    }
}
