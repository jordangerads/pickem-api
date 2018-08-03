package com.gci.pickem.util;

import org.junit.Assert;
import org.junit.Test;

public class ScheduleUtilTest {

    @Test
    public void testGet24HourTime() {
        int asserts = 0;

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

                Assert.assertEquals(expected, ScheduleUtil.get24HourTime(time));
                asserts++;
            }
        }

        for (int i = 0; i < 60; i++) {
            String minute = Integer.toString(i);
            if (i < 10) {
                minute = "0" + minute;
            }

            String time = "12:" + minute + "PM";
            Assert.assertEquals(time.replace("PM", ""), ScheduleUtil.get24HourTime(time));
            asserts++;
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

                Assert.assertEquals(expected, ScheduleUtil.get24HourTime(time));
                asserts++;
            }
        }

        // Verify we actually tested all 1440 minutes in the day!
        Assert.assertEquals(1440, asserts);
    }
}
