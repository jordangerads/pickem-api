package com.qci.pickem.model.mysportsfeeds;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class FullGameSchedule {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss a")
    private Date lastUpdatedOn;

    @JsonProperty("gameentry")
    private List<GameEntry> gameEntries;

    public Date getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(Date lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public List<GameEntry> getGameEntries() {
        return gameEntries;
    }

    public void setGameEntries(List<GameEntry> gameEntries) {
        this.gameEntries = gameEntries;
    }
}
