package com.gci.pickem.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class WeekGames {
    private final List<Game> games;

    public WeekGames(List<Game> games) {
        this.games = games;
    }

    public List<Game> getGames() {
        return ImmutableList.copyOf(games);
    }
}
