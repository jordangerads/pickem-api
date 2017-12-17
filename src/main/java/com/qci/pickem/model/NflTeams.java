package com.qci.pickem.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public enum NflTeams {
    // NFC North
    CHICAGO_BEARS("CHI", "Bears", "Chicago Bears"),
    DETROIT_LIONS("DET", "Lions", "Detroit Lions"),
    GREEN_BAY_PACKERS("GB", "Packers", "Green Bay Packers"),
    MINNESOTA_VIKINGS("MIN", "Vikings", "Minnesota Vikings"),

    // NFC South
    ATLANTA_FALCONS("ATL", "Falcons", "Atlanta Falcons"),
    CAROLINA_PANTHERS("CAR", "Panthers", "Carolina Panthers"),
    NEW_ORLEANS_SAINTS("NO", "Saints", "New Orleans Saints"),
    TAMPA_BAY_BUCCANEERS("TB", "Buccaneers", "Tampa Bay Buccaneers"),

    // NFC East
    DALLAS_COWBOYS("DAL", "Cowboys", "Dallas Cowboys"),
    NEW_YORK_GIANTS("NYG", "Giants", "New York Giants"),
    PHILADELPHIA_EAGLES("PHI", "Eagles", "Philadelphia Eagles"),
    WASHINGTON_REDSKINS("WAS", "Redskins", "Washington Redskins"),

    // NFC West
    ARIZONA_CARDINALS("ARI", "Cardinals", "Arizona Cardinals"),
    LOS_ANGELES_RAMS("LA", "Rams", "Los Angeles Rams", "STL"), // Should be LAR!
    SAN_FRANCISCO_49ERS("SF", "49ers", "San Francisco 49ers"),
    SEATTLE_SEAHAWKS("SEA", "Seahawks", "Seattle Seahawks"),

    // AFC North
    BALTIMORE_RAVENS("BAL", "Ravens", "Baltimore Ravens"),
    CINCINATTI_BENGALS("CIN", "Bengals", "Cincinatti Bengals"),
    CLEVELAND_BROWNS("CLE", "Browns", "Cleveland Browns"),
    PITTSBURGH_STEELERS("PIT", "Steelers", "Pittsburgh Steelers"),

    // AFC South
    HOUSTON_TEXANS("HOU", "Texans", "Houston Texans"),
    INDIANAPOLIS_COLTS("IND", "Colts", "Indianapolis Colts"),
    JACKSONVILLE_JAGUARS("JAX", "Jaguars", "Jacksonville Jaguars"),
    TENNESSEE_TITANS("TEN", "Titans", "Tennessee Titans"),

    // AFC East
    BUFFALO_BULLS("BUF", "Bills", "Buffalo Bills"),
    MIAMI_DOLPHINS("MIA", "Dolphins", "Miami Dolphins"),
    NEW_ENGLAND_PATRIOTS("NE", "Patriots", "New England Patriots"),
    NEW_YORK_JETS("NYJ", "Jets", "New York Jets"),

    // AFC West
    DENVER_BRONCOS("DEN", "Broncos", "Denver Broncos"),
    KANSAS_CITY_CHIEFS("KC", "Chiefs", "Kansas City Chiefs"),
    LOS_ANGELES_CHARGERS("LAC", "Chargers", "Los Angeles Chargers", "SD"),
    OAKLAND_RAIDERS("OAK", "Raiders", "Oakland Raiders"),

    // Need an other for exceptional cases
    UNKNOWN("UNK", "Unknown", "Unknown");

    private final String displayCode;
    private final String shortName;
    private final String fullName;
    private final String priorCode;

    private static final Map<String, NflTeams> TEAMS_BY_CODE;

    private static final Logger log = LoggerFactory.getLogger(NflTeams.class);

    static {
        ImmutableMap.Builder<String, NflTeams> builder = ImmutableMap.builder();

        Lists.newArrayList(NflTeams.values()).forEach(nflTeam -> {
            builder.put(nflTeam.displayCode, nflTeam);
            if (StringUtils.isNotBlank(nflTeam.priorCode)) {
                builder.put(nflTeam.priorCode, nflTeam);
            }
        });

        TEAMS_BY_CODE = builder.build();
    }

    NflTeams(String displayCode, String shortName, String fullName) {
        this.displayCode = displayCode;
        this.shortName = shortName;
        this.fullName = fullName;
        this.priorCode = null;
    }

    NflTeams(String displayCode, String shortName, String fullName, String priorCode) {
        this.displayCode = displayCode;
        this.shortName = shortName;
        this.fullName = fullName;
        this.priorCode = priorCode;
    }

    public String getDisplayCode() {
        return displayCode;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public static NflTeams getTeamByCode(String code) {
        if (code == null) {
            return UNKNOWN;
        }

        NflTeams nflTeam = TEAMS_BY_CODE.get(code);
        if (nflTeam == null) {
            log.warn("Unexpected team code {} encountered", code);
            return UNKNOWN;
        }

        return nflTeam;
    }
}
