package com.gci.pickem.service.schedule;

import com.gci.pickem.data.Game;
import com.gci.pickem.model.GamesList;
import com.gci.pickem.model.TeamView;
import com.gci.pickem.model.mysportsfeeds.*;
import com.gci.pickem.service.game.GamesService;
import com.gci.pickem.service.mysportsfeeds.MySportsFeedsService;
import com.gci.pickem.service.team.TeamService;
import com.gci.pickem.util.ScheduleUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    private MySportsFeedsService mySportsFeedsService;
    private TeamService teamService;
    private GamesService gamesService;

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
    @Transactional
    public GamesList getGamesForNextDays(int days) {
        FullGameSchedule schedule = mySportsFeedsService.getGamesUntilDaysFromNow(days);

        if (CollectionUtils.isNotEmpty(schedule.getGameEntries())) {
            log.debug("Found {} games on today's schedule.", schedule.getGameEntries().size());
            // Process the games if we don't already have them.
            List<Game> games = processNewData(schedule.getGameEntries());
            return new GamesList(games.stream().map(this::getGameView).collect(Collectors.toList()));
        } else {
            log.debug("No scheduled games found for today.");
        }

        // No games today, nothing to check.
        return new GamesList();
    }

    @Override
    @Transactional
    public GamesList getGamesForSeasonAndWeek(int season, int week) {
        Collection<Game> existingGames = gamesService.findAllBySeasonAndWeek(season, week);
        if (CollectionUtils.isEmpty(existingGames)) {
            // Games don't yet exist. Grab the data from MSF.
            FullGameSchedule schedule = mySportsFeedsService.getGamesForSeasonAndWeek(season, week);

            // Add ALL of the games from the requested schedule to the database. Return only those for the requested week.
            List<Game> newGames = processNewData(schedule.getGameEntries());

            List<com.gci.pickem.model.Game> games =
                newGames.stream()
                    .map(this::getGameView)
                    .sorted(Comparator.comparing(com.gci.pickem.model.Game::getGameTime))
                    .collect(Collectors.toList());

            return new GamesList(games);
        } else {
            return new GamesList(existingGames.stream().map(this::getGameView).collect(Collectors.toList()));
        }
    }

    private Game getGameByExternalId(int externalId, int week, Instant date) {
        Game game = gamesService.findByExternalId(externalId);
        if (game == null) {
            // We have a score, but not the actual game somehow... Request the week's games to fill in.
            getGamesForSeasonAndWeek(ScheduleUtil.getSeasonForDate(date), week);

            // Try to get it again.
            game = gamesService.findByExternalId(externalId);
            if (game == null) {
                throw new RuntimeException(
                        String.format("Unable to find game with external ID %d in database.", externalId));
            }
        }

        return game;
    }

    @Override
    @Transactional
    public void processScoresForDate(Instant date) {
        Scoreboard scoreboard = mySportsFeedsService.getFinalGameScores(date);
        if (scoreboard == null || CollectionUtils.isEmpty(scoreboard.getGameScores())) {
            log.info("No final scores found for date {}", date);
            return;
        }

        List<GameScore> scores = scoreboard.getGameScores();
        for (GameScore gameScore : scores) {
            try {
                GameEntry gameEntry = gameScore.getGame();
                if (!"true".equalsIgnoreCase(gameScore.getIsCompleted())) {
                    log.warn("Received non-complete game in response for game with external ID %d", gameEntry.getId());
                    continue;
                }

                Game game = getGameByExternalId(gameEntry.getId(), gameEntry.getWeek(), date);
                if (game.getWinningTeamId() != null) {
                    // Game has already been processed. Nothing to do.
                    continue;
                }

                int home = gameScore.getHomeScore();
                int away = gameScore.getAwayScore();

                if (home != away) {
                    // There is a winner. Sad day.
                    int winningTeamId =
                        home > away ?
                            gameEntry.getHomeTeam().getId() :
                            gameEntry.getAwayTeam().getId();

                    com.gci.pickem.data.Team team = teamService.findByExternalId((long) winningTeamId);
                    if (team == null) {
                        throw new RuntimeException(String.format("No team found for external ID %d", winningTeamId));
                    }

                    log.info("Game with ID {} is complete with winning team ID {}.", game.getGameId(), team.getTeamId());

                    game.setWinningTeamId(team.getTeamId());
                    game.setGameComplete(true);

                    gamesService.saveGame(game);
                }
            } catch (Exception e) {
                log.error("Error occurred while attempting to process game score: {}", e.getMessage());
            }
        }
    }

    private com.gci.pickem.model.Game getGameView(Game game) {
        com.gci.pickem.model.Game model = new com.gci.pickem.model.Game();

        model.setGameId(game.getGameId());
        model.setGameTimeEpoch(game.getGameTimeEpoch());
        model.setHomeTeam(new TeamView(teamService.findById(game.getHomeTeamId())));
        model.setAwayTeam(new TeamView(teamService.findById(game.getAwayTeamId())));

        // TODO: remove this!
        model.setGameTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(game.getGameTimeEpoch()), ZoneId.of("America/New_York")));

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

    private Game processGame(GameEntry entry) {
        com.gci.pickem.data.Game game = gamesService.findByExternalId(entry.getId());

        if (game != null) {
            return game;
        }

        // Game isn't in the DB yet.
        game = new Game();

        long gameTimeEpoch = ScheduleUtil.getUtcGameTime(entry);
        Instant gameTime = Instant.ofEpochMilli(gameTimeEpoch);

        game.setSeason(ScheduleUtil.getSeasonForDate(gameTime));
        game.setWeek(entry.getWeek());
        game.setExternalId(entry.getId());

        game.setGameTimeEpoch(gameTimeEpoch);

        com.gci.pickem.data.Team away = teamService.findByExternalId((long) entry.getAwayTeam().getId());
        game.setAwayTeamId(away.getTeamId());

        com.gci.pickem.data.Team home = teamService.findByExternalId((long) entry.getHomeTeam().getId());
        game.setHomeTeamId(home.getTeamId());

        // Check if the game has been finalized.
        try {
            GameScore score = mySportsFeedsService.getGameScore(gameTime, entry.getId());
            game.setGameComplete(Boolean.valueOf(score.getIsCompleted()));
            if (game.getGameComplete()) {
                game.setWinningTeamId(score.getHomeScore().compareTo(score.getAwayScore()) > 0 ? home.getTeamId() : away.getTeamId());
            }
        } catch (RuntimeException e) {
            log.debug("No score found for game with external ID {}: {}", entry.getId(), e.getMessage());
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
