package com.qci.pickem.model.mysportsfeeds;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FullGameScheduleResponse {

    @JsonProperty(value = "fullgameschedule")
    private FullGameSchedule fullGameSchedule;

    public FullGameSchedule getFullGameSchedule() {
        return fullGameSchedule;
    }

    public void setFullGameSchedule(FullGameSchedule fullGameSchedule) {
        this.fullGameSchedule = fullGameSchedule;
    }
}
