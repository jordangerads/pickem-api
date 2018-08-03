package com.gci.pickem.service.schedule;

import com.gci.pickem.service.game.GamesService;
import com.gci.pickem.service.mysportsfeeds.MySportsFeedsService;
import com.gci.pickem.service.team.TeamService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleServiceImplTest {

    @Mock private MySportsFeedsService mySportsFeedsService;
    @Mock private TeamService teamService;
    @Mock private GamesService gamesService;

    private ScheduleServiceImpl service;

    @Before
    public void setup() {
        service = new ScheduleServiceImpl(mySportsFeedsService, teamService, gamesService);
    }

    @Test
    public void testGet24HourTime() {
        // Test 00:00-11:59
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 60; j++) {
                String time = Integer.toString(i) + ":";
                if (j < 10) {
                    time += "0";
                }

                time += Integer.toString(j) + "AM";

                String expected = time.replace("AM", "");
                if (i < 10) {
                    expected = "0" + expected;
                }

                Assert.assertEquals(expected, service.get24HourTime(time));
            }
        }

        for (int i = 0; i < 59; i++) {
            String minute = Integer.toString(i);
            if (i < 10) {
                minute = "0" + minute;
            }

            String time = "12:" + minute + "PM";
            Assert.assertEquals(time.replace("PM", ""), service.get24HourTime(time));
        }

        // Test 13:00-23:59
        for (int i = 1; i < 12; i++) {
            for (int j = 0; j < 60; j++) {
                String time = Integer.toString(i) + ":";
                if (j < 10) {
                    time += "0";
                }

                time += Integer.toString(j) + "PM";

                String expected = Integer.toString(i + 12) + ":";
                if (j < 10) {
                    expected += "0";
                }

                expected += Integer.toString(j);

                Assert.assertEquals(expected, service.get24HourTime(time));
            }
        }
    }
}
