package com.gci.pickem.service.schedule;

import com.gci.pickem.data.Game;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

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
    public WeekGames getGamesForSeasonAndWeek(int season, int week) {
        Collection<Game> existingGames = gamesService.findAllBySeasonAndWeek(season, week);
        if (CollectionUtils.isEmpty(existingGames)) {
            // Games don't yet exist. Grab the data from MSF.
            FullGameSchedule schedule = mySportsFeedsService.getGamesForSeasonAndWeek(season, week);

            List<GameEntry> weekEntries =
                schedule.getGameEntries().stream()
                .filter(entry -> entry.getWeek().equals(week))
                .collect(Collectors.toList());

            processNewData(weekEntries);

            List<com.gci.pickem.model.Game> games =
                schedule.getGameEntries().stream()
                    .filter(entry -> entry.getWeek().equals(week))
                    .map(com.gci.pickem.model.Game::new)
                    .sorted(Comparator.comparing(com.gci.pickem.model.Game::getGameTime))
                    .collect(Collectors.toList());

            return new WeekGames(games);
        } else {
            return new WeekGames(existingGames.stream().map(com.gci.pickem.model.Game::new).collect(Collectors.toList()));
        }
    }

    private void processNewData(List<GameEntry> gameEntries) {
        for (GameEntry entry : gameEntries) {
            // Process teams before the game.
            processTeam(entry.getHomeTeam());
            processTeam(entry.getAwayTeam());

            processGame(entry);
        }
    }

    private void processGame(GameEntry entry) {
        com.gci.pickem.data.Game game = gamesService.findByExternalId(entry.getId());
        if (game == null) {
            // Game isn't in the DB yet.
            game = new com.gci.pickem.data.Game();
            game.setSeason(SeasonUtil.getSeasonForDate(entry.getDate()));
            game.setWeek(entry.getWeek());
            game.setExternalId(entry.getId());

            Date gameDate = java.sql.Date.valueOf(LocalDateTime.ofInstant(entry.getDate().toInstant(), ZoneId.of("UTC")).toLocalDate());

            game.setGameTime(gameDate);

            com.gci.pickem.data.Team away = teamService.findByExternalId((long) entry.getAwayTeam().getId());
            game.setAwayTeamId(away.getTeamId());

            com.gci.pickem.data.Team home = teamService.findByExternalId((long) entry.getHomeTeam().getId());
            game.setHomeTeamId(home.getTeamId());

            // Check if the game has been finalized.
            GameScore score = mySportsFeedsService.getGameScore(gameDate, entry.getId());
            game.setGameComplete(Boolean.valueOf(score.getIsCompleted()));
            game.setWinningTeamId(score.getHomeScore().compareTo(score.getAwayScore()) > 0 ? home.getTeamId() : away.getTeamId());

            // This needs to be transactional here!
            gamesService.saveGame(game);
        }
    }

    private void processTeam(Team external) {
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
