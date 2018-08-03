package com.gci.pickem.service.mysportsfeeds;

public enum MySportsFeedEndpoint {
    FULL_GAME_SCHEDULE("full_game_schedule"),
    SCOREBOARD("scoreboard");

    private final String value;

    MySportsFeedEndpoint(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
