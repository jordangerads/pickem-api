package com.qci.pickem.model.mysportsfeeds;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class GameEntry {
    private Integer id;
    private Integer week;
    private String scheduleStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date originalDate;

    // e.g. "1:00PM"
    private String originalTime;

    private String delayedOrPostponedReason;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    // e.g. "1:00PM"
    private String time;

    private Team awayTeam;
    private Team homeTeam;

    private String location;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Date getOriginalDate() {
        return originalDate;
    }

    public void setOriginalDate(Date originalDate) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
