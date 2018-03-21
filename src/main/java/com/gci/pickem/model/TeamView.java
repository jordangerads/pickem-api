package com.gci.pickem.model;

import com.gci.pickem.data.Team;

public class TeamView {

    private Long teamId;
    private String city;
    private String teamName;
    private String abbreviation;

    public TeamView() {
        // Default constructor for serialization and such.
    }

    public TeamView(Team team) {
        this.teamId = team.getTeamId();
        this.city = team.getCity();
        this.teamName = team.getTeamName();
        this.abbreviation = team.getAbbreviation();
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getCity() {
        return city;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
}
