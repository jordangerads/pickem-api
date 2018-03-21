package com.gci.pickem.controller;

import com.gci.pickem.exception.MissingRequiredDataException;
import com.gci.pickem.model.Game;
import com.gci.pickem.model.WeekGames;
import org.junit.Assert;
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
public class ScheduleControllerTest {

    @Autowired private ScheduleController scheduleController;

    @Test
    public void testGetGames() {
        WeekGames gamesByWeekOfYear = scheduleController.getGamesByWeekOfYear(2016, 1);

        assertEquals(16, gamesByWeekOfYear.getGames().size());

        Game game = gamesByWeekOfYear.getGames().get(0);

        Assert.assertEquals("Denver", game.getHomeTeam().getCity());
        Assert.assertEquals("Broncos", game.getHomeTeam().getTeamName());
        Assert.assertEquals("Carolina", game.getAwayTeam().getCity());
        Assert.assertEquals("Panthers", game.getAwayTeam().getTeamName());
        assertEquals(LocalDateTime.parse("2016-09-07T20:30", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")), game.getGameTime());
    }

    @Test(expected = MissingRequiredDataException.class)
    public void testGetGamesNullYear() {
        scheduleController.getGamesByWeekOfYear(null, 1);
    }

    @Test(expected = MissingRequiredDataException.class)
    public void testGetGamesNullWeek() {
        scheduleController.getGamesByWeekOfYear(2017, null);
    }
}