package com.gci.pickem.service.schedule;

import com.gci.pickem.data.Game;
import com.gci.pickem.model.TeamView;
import com.gci.pickem.model.WeekGames;
import com.gci.pickem.model.mysportsfeeds.FullGameSchedule;
import com.gci.pickem.model.mysportsfeeds.GameEntry;
import com.gci.pickem.model.mysportsfeeds.GameScore;
import com.gci.pickem.model.mysportsfeeds.Team;
import com.gci.pickem.service.game.GamesService;
import com.gci.pickem.service.mysportsfeeds.MySportsFeedsService;
import com.gci.pickem.service.team.TeamService;
import com.gci.pickem.util.SeasonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private MySportsFeedsService mySportsFeedsService;
    private TeamService teamService;
    private GamesService gamesService;

    private static final Pattern GAME_TIME_PATTERN = Pattern.compile("(1?[0-9]):([0-5][0-9])([AP]M)");

    @Autowired
    ScheduleServiceImpl(
        MySportsFeedsService mySportsFeedsService,
        TeamService teamService,
        GamesService gamesService
    ) {
        this.mySportsFeedsService = mySportsFeedsService;
        this.teamService = teamService;
        this.gamesService = gamesService;
    }

    @Override
    public WeekGames getGamesForSeasonAndWeek(int season, int week) {
        Collection<Game> existingGames = gamesService.findAllBySeasonAndWeek(season, week);
        if (CollectionUtils.isEmpty(existingGames)) {
            // Games don't yet exist. Grab the data from MSF.
            FullGameSchedule schedule = mySportsFeedsService.getGamesForSeasonAndWeek(season, week);

            // Add ALL of the games from the requested schedule to the database.
            // Return only those for the requested week.
            List<Game> newGames = processNewData(schedule.getGameEntries());
            List<com.gci.pickem.model.Game> games =
                newGames.stream()
                    .filter(entry -> entry.getWeek().equals(week))
                    .map(this::getGameView)
                    .sorted(Comparator.comparing(com.gci.pickem.model.Game::getGameTime))
                    .collect(Collectors.toList());

            return new WeekGames(games);
        } else {
            return new WeekGames(existingGames.stream().map(this::getGameView).collect(Collectors.toList()));
        }
    }

    private com.gci.pickem.model.Game getGameView(Game game) {
        com.gci.pickem.model.Game model = new com.gci.pickem.model.Game();

        model.setGameId(game.getGameId());
        model.setGameTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(game.getGameTimeEpoch()), ZoneId.of("America/New_York")));

        model.setHomeTeam(new TeamView(teamService.findById(game.getHomeTeamId())));
        model.setAwayTeam(new TeamView(teamService.findById(game.getAwayTeamId())));

        return model;
    }

    private List<Game> processNewData(List<GameEntry> gameEntries) {
        List<Game> games = new ArrayList<>();

        for (GameEntry entry : gameEntries) {
            // Process teams before the game.
            processTeam(entry.getHomeTeam());
            processTeam(entry.getAwayTeam());

            games.add(processGame(entry));
        }

        return games;
    }

    private long getUtcGameTime(GameEntry gameEntry) {
        String date = gameEntry.getDate();
        String time = get24HourTime(gameEntry.getTime());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        TemporalAccessor temporalAccessor = formatter.parse(String.format("%s %s", date, time));
        LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("America/New_York"));
        return Instant.from(zonedDateTime).toEpochMilli();
    }

    String get24HourTime(String time) {
        Matcher m = GAME_TIME_PATTERN.matcher(time);
        if (!m.matches()) {
            throw new RuntimeException(String.format("Unexpected game time format: %s", time));
        }

        String militaryTime = "";

        String hour = m.group(1);

        String timeOfDay = m.group(3);
        if ("PM".equals(timeOfDay) && !"12".equals(hour)) {
            militaryTime += Integer.toString(Integer.parseInt(hour) + 12);
        } else if ("AM".equals(timeOfDay) && "12".equals(hour)) {
            militaryTime += "00";
        } else {
            Integer intHour = Integer.parseInt(hour);
            if (intHour < 10) {
                militaryTime += "0";
            }

            militaryTime += hour;
        }

        militaryTime += ":" + m.group(2);

        return militaryTime;
    }

    private Game processGame(GameEntry entry) {
        com.gci.pickem.data.Game game = gamesService.findByExternalId(entry.getId());

        if (game != null) {
            return game;
        }

        // Game isn't in the DB yet.
        game = new com.gci.pickem.data.Game();

        long gameTimeEpoch = getUtcGameTime(entry);
        Instant gameTime = Instant.ofEpochMilli(gameTimeEpoch);

        game.setSeason(SeasonUtil.getSeasonForDate(gameTime));
        game.setWeek(entry.getWeek());
        game.setExternalId(entry.getId());

        game.setGameTimeEpoch(getUtcGameTime(entry));

        com.gci.pickem.data.Team away = teamService.findByExternalId((long) entry.getAwayTeam().getId());
        game.setAwayTeamId(away.getTeamId());

        com.gci.pickem.data.Team home = teamService.findByExternalId((long) entry.getHomeTeam().getId());
        game.setHomeTeamId(home.getTeamId());

        // Check if the game has been finalized.
        GameScore score = mySportsFeedsService.getGameScore(gameTime, entry.getId());
        game.setGameComplete(Boolean.valueOf(score.getIsCompleted()));
        if (game.getGameComplete()) {
            game.setWinningTeamId(score.getHomeScore().compareTo(score.getAwayScore()) > 0 ? home.getTeamId() : away.getTeamId());
        }

        // This needs to be transactional here!
        return gamesService.saveGame(game);
    }

    private void processTeam(Team external) {
        // TODO: Possible this data could get out of sync. We should probably compare everything and update our record.
        com.gci.pickem.data.Team team = teamService.findByExternalId((long) external.getId());
        if (team == null) {
            // Team doesn't already exist.
            team = new com.gci.pickem.data.Team();
            team.setAbbreviation(external.getAbbreviation());
            team.setCity(external.getCity());
            team.setTeamName(external.getName());
            team.setExternalId((long) external.getId());

            // This needs to be transactional here!
            teamService.createTeam(team);
        }
    }
}
