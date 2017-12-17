package com.qci.pickem.controller;

import com.qci.pickem.exception.MissingRequiredDataException;
import com.qci.pickem.model.Game;
import com.qci.pickem.model.NflTeams;
import com.qci.pickem.model.WeekGames;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GamesControllerTest {

    @Autowired private GamesController gamesController;

    @Test
    public void testGetGames() {
        WeekGames gamesByWeekOfYear = gamesController.getGamesByWeekOfYear(2016, 1);

        assertEquals(16, gamesByWeekOfYear.getGames().size());

        Game game = gamesByWeekOfYear.getGames().get(0);

        assertEquals(NflTeams.DENVER_BRONCOS.getFullName(), game.getHomeTeam());
        assertEquals(NflTeams.CAROLINA_PANTHERS.getFullName(), game.getAwayTeam());
        assertEquals(LocalDateTime.parse("2016-09-07T20:30", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")), game.getGameTime());
    }

    @Test(expected = MissingRequiredDataException.class)
    public void testGetGamesNullYear() {
        gamesController.getGamesByWeekOfYear(null, 1);
    }

    @Test(expected = MissingRequiredDataException.class)
    public void testGetGamesNullWeek() {
        gamesController.getGamesByWeekOfYear(2017, null);
    }
}