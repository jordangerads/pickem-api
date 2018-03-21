package com.gci.pickem.data;

import com.gci.pickem.util.TimestampConverter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(name = "season", nullable = false)
    private Integer season;

    @Column(name = "week", nullable = false)
    private Integer week;

    @Column(name = "home_team_id", nullable = false)
    private Long homeTeamId;

    @Column(name = "away_team_id", nullable = false)
    private Long awayTeamId;

    @Column(name = "game_complete")
    private Boolean isGameComplete = false;

    @Column(name = "winning_team_id")
    private Long winningTeamId;

    @Column(name = "game_time")
    @Convert(converter = TimestampConverter.class)
    private Instant gameTime;

    @Column(name = "external_game_id")
    private Integer externalId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "home_team_id", referencedColumnName = "team_id", insertable = false, updatable = false)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "away_team_id", referencedColumnName = "team_id", insertable = false, updatable = false)
    private Team awayTeam;

    public Game() {
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Long getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(Long homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public Long getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(Long awayTeamId) {
        this.awayTeamId = awayTeamId;
    }

    public Boolean getGameComplete() {
        return isGameComplete;
    }

    public void setGameComplete(Boolean gameComplete) {
        isGameComplete = gameComplete;
    }

    public Long getWinningTeamId() {
        return winningTeamId;
    }

    public void setWinningTeamId(Long winningTeamId) {
        this.winningTeamId = winningTeamId;
    }

    public Instant getGameTime() {
        return gameTime;
    }

    public void setGameTime(Instant gameTime) {
        this.gameTime = gameTime;
    }

    public Integer getExternalId() {
        return externalId;
    }

    public void setExternalId(Integer externalId) {
        this.externalId = externalId;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }
}
