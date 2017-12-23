package com.qci.pickem.controller;

import com.qci.pickem.model.NflTeams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NflTeamsControllerTest {

    @Autowired private NflTeamsController controller;

    @Test
    public void testValidTeam() {
        assertEquals(NflTeams.MINNESOTA_VIKINGS, controller.getTeamByCode("MIN"));
    }

    @Test
    public void testValidTeamWithAlternateCode() {
        assertEquals(NflTeams.LOS_ANGELES_CHARGERS, controller.getTeamByCode("LAC"));
        assertEquals(NflTeams.LOS_ANGELES_CHARGERS, controller.getTeamByCode("SD"));
    }

    @Test
    public void testInvalidTeam() {
        assertEquals(NflTeams.UNKNOWN, controller.getTeamByCode("BAD"));
    }
}