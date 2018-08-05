package com.gci.pickem.model;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public final class GamesList {
    private final List<Game> games;

    public GamesList() {
        this.games = new ArrayList<>();
    }

    public GamesList(List<Game> games) {
        this.games = games;
    }

    public List<Game> getGames() {
        return ImmutableList.copyOf(games);
    }
}
