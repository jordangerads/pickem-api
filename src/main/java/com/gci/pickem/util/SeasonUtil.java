package com.gci.pickem.util;

import java.util.Calendar;
import java.util.Date;

public abstract class SeasonUtil {

    public static int getSeasonForDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        /*
         * The "season" corresponds to the earliest year games were played. If this game
         * took place late in the year, then the year we have is the season. If the game
         * took place in the beginning of the year, then we need to subtract one from the
         * year we have to get the season.
         */
        return month >= Calendar.AUGUST ? year : year - 1;
    }
}
