package com.gci.pickem.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public interface SeasonUtil {

    static int getSeasonForDate(Instant instant) {
        /*
         * The "season" corresponds to the earliest year games were played. If this game
         * took place late in the year, then the year we have is the season. If the game
         * took place in the beginning of the year, then we need to subtract one from the
         * year we have to get the season.
         */
        LocalDate localDate = instant.atZone(ZoneId.of("America/New_York")).toLocalDate();
        return localDate.getMonthValue() >= Month.AUGUST.getValue() ? localDate.getYear() : localDate.getYear() - 1;
    }
}
