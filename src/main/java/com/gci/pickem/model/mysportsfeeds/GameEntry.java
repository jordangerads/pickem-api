package com.gci.pickem.model.mysportsfeeds;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class GameEntry {

    private Integer id;

    private Integer week;
    private String scheduleStatus;

    private String originalDate;

    // e.g. "1:00PM"
    private String originalTime;

    private String delayedOrPostponedReason;

    private String date;

    // e.g. "1:00PM"
    private String time;

    private Team awayTeam;
    private Team homeTeam;

    private String location;

    @JsonGetter("id")
    public Integer getId() {
        return id;
    }

    @JsonSetter("id")
    public void setIdForSchedule(Integer id) {
        this.id = id;
    }

    @JsonSetter("ID")
    public void setIdForScore(Integer id) {
        this.id = id;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public String getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(String scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public String getOriginalDate() {
        return originalDate;
    }

    public void setOriginalDate(String originalDate) {
        this.originalDate = originalDate;
    }

    public String getOriginalTime() {
        return originalTime;
    }

    public void setOriginalTime(String originalTime) {
        this.originalTime = originalTime;
    }

    public String getDelayedOrPostponedReason() {
        return delayedOrPostponedReason;
    }

    public void setDelayedOrPostponedReason(String delayedOrPostponedReason) {
        this.delayedOrPostponedReason = delayedOrPostponedReason;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
