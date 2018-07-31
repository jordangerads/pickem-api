package com.gci.pickem.service.picks;

import com.gci.pickem.data.*;
import com.gci.pickem.model.GamePick;
import com.gci.pickem.model.UserPicksRequest;
import com.gci.pickem.model.WeekGames;
import com.gci.pickem.repository.GameRepository;
import com.gci.pickem.repository.UserRepository;
import com.gci.pickem.model.ScoringMethod;
import com.gci.pickem.repository.PickRepository;
import com.gci.pickem.repository.PoolRepository;
import com.gci.pickem.service.mail.MailService;
import com.gci.pickem.service.mail.SendEmailRequest;
import com.gci.pickem.service.schedule.ScheduleService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PickServiceImpl implements PickService {
    private static final Logger log = LoggerFactory.getLogger(PickServiceImpl.class);

    private PickRepository pickRepository;
    private GameRepository gameRepository;
    private PoolRepository poolRepository;
    private UserRepository userRepository;
    private ScheduleService scheduleService;
    private MailService mailService;

    @Autowired
    PickServiceImpl(
        PickRepository pickRepository,
        GameRepository gameRepository,
        PoolRepository poolRepository,
        UserRepository userRepository,
        ScheduleService scheduleService,
        MailService mailService
    ) {
        this.pickRepository = pickRepository;
        this.gameRepository = gameRepository;
        this.poolRepository = poolRepository;
        this.userRepository = userRepository;
        this.scheduleService = scheduleService;
        this.mailService = mailService;
    }

    @Override
    public void saveUserPicks(UserPicksRequest request) {
        if (request == null || request.getUserId() == null || request.getPoolId() == null ||
            CollectionUtils.isEmpty(request.getGamePicks())) {
            log.info("Insufficient information received to record picks for user.");
            return;
        }

        List<GamePick> picks = request.getGamePicks();

        validateUserValidForPool(request.getUserId(), request.getPoolId());
        validateGames(picks);
        validatePicksValidForPool(request.getPoolId(), picks);

        // Everything appears to be valid. Let's save them picks!
        int season = getSeason(picks);
        int week = getWeek(picks);

        Set<Pick> existingPicks = pickRepository.getPicks(request.getUserId(), request.getPoolId(), season, week);
        if (CollectionUtils.isEmpty(existingPicks)) {
            // User has not saved any picks for this season/week/pool yet. Make all new ones.
            List<Pick> toCreate =
                request.getGamePicks().stream()
                    .map(gamePick -> {
                        Pick pick = new Pick();

                        pick.setUserId(request.getUserId());
                        pick.setPoolId(request.getPoolId());
                        pick.setGameId(gamePick.getGameId());
                        pick.setConfidence(gamePick.getConfidence());
                        pick.setChosenTeamId(gamePick.getChosenTeamId());

                        return pick;
                    })
                    .collect(Collectors.toList());

            pickRepository.save(toCreate);
        } else {
            // User has existing picks saved, update confidences.
            Map<Long, GamePick> picksByGameId = new HashMap<>();
            picks.forEach(pick -> picksByGameId.put(pick.getGameId(), pick));

            existingPicks.forEach(pick -> pick.setConfidence(picksByGameId.get(pick.getGameId()).getConfidence()));

            pickRepository.save(existingPicks);
        }
    }

    @Override
    public List<Integer> getConfidenceValues(Long poolId, Integer season, Integer week) {
        Pool pool = poolRepository.findOne(poolId);
        if (pool == null) {
            throw new RuntimeException(String.format("No pool found for poolId %d", poolId));
        }

        ScoringMethod method = ScoringMethod.getScoringMethodById(pool.getScoringMethod());
        if (method == null) {
            throw new RuntimeException(String.format("No scoring method found for pool with ID %d", poolId));
        }

        WeekGames weekGames = scheduleService.getGamesForSeasonAndWeek(season, week);

        switch (method) {
            case ABSOLUTE:
                return Collections.nCopies(weekGames.getGames().size(), 1);
            case SIXTEEN_DOWN:
                List<Integer> values = new ArrayList<>();

                int nextVal = 16;
                for (Iterator<com.gci.pickem.model.Game> iter = weekGames.getGames().iterator(); iter.hasNext(); iter.next()) {
                    values.add(nextVal--);
                }

                return values;
            default:
                throw new RuntimeException(String.format("Unexpected method %s found for pool with ID %d", method, poolId));
        }
    }

    @Override
    @Transactional
    public void notifyUsersWithoutPicks() {
        notifyUsersWithoutPicks(LocalDate.now(ZoneId.of("America/Chicago")));
    }

    void notifyUsersWithoutPicks(LocalDate today) {
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
        LocalDateTime tomorrowMidnight = todayMidnight.plusDays(1);

        // Find all games
        List<Game> games =
            gameRepository.findByGameTimeBetween(
                todayMidnight.toInstant(ZoneOffset.UTC),
                tomorrowMidnight.toInstant(ZoneOffset.UTC));

        if (games.isEmpty()) {
            // There are no games in this 24-hour period, no alerts need to be sent out.
            return;
        }

        List<Long> gameIds = games.stream().map(Game::getGameId).collect(Collectors.toList());

        // Find all users in pools who haven't made picks for the given games
        Set<Object[]> usersMissingPicks = userRepository.getUsersWithMissingPicks(gameIds, gameIds.size());

        // This could get expensive later. Watch out.
        Map<Long, Pool> poolCache = new HashMap<>();

        List<SendEmailRequest> requests = new ArrayList<>();
        for (Object[] result : usersMissingPicks) {
            try {
                Long userId = (Long) result[0];
                Long poolId = (Long) result[1];

                User user = userRepository.findOne(userId);
                validateUserValidForPool(user.getUserId(), poolId);

                // User belongs to pool.
                Pool pool = getPool(poolId, poolCache);

                SendEmailRequest request = new SendEmailRequest();
                request.setRecipientName(user.getFirstName());
                request.setRecipientEmail(user.getEmail());

                Map<String, Object> requestData = new HashMap<>();
                requestData.put("poolName", pool.getPoolName());

                Game game = games.get(0);
                Set<Pick> picks = pickRepository.getPicks(userId, poolId, game.getSeason(), game.getWeek());

                // Start by assuming they're missing everything.
                Set<Long> missingPickGameIds = new HashSet<>(gameIds);
                if (!picks.isEmpty()) {
                    // Remove any games that the user has a saved pick for.
                    missingPickGameIds.removeAll(
                        picks.stream()
                            .filter(pick -> gameIds.contains(pick.getGameId()) && pick.getConfidence() != null)
                            .map(Pick::getGameId)
                            .collect(Collectors.toList()));
                }

                Iterable<Game> gamesNeedingPicks = gameRepository.findAll(missingPickGameIds);
                List<Map<String, String>> missingGames = new ArrayList<>();
                for (Game needingPick : gamesNeedingPicks) {
                    Map<String, String> missingGame = new HashMap<>();

                    missingGame.put("awayTeamName", needingPick.getAwayTeam().getTeamName());
                    missingGame.put("homeTeamName", needingPick.getHomeTeam().getTeamName());
                    missingGame.put("gameTime", needingPick.getGameTime().toString());

                    missingGames.add(missingGame);
                }

                requestData.put("missingGames", missingGames);

                request.setRequestData(requestData);

                requests.add(request);
            } catch (Exception e) {
                log.error("{}", e);
            }

            mailService.sendEmails(requests);
        }

        log.info("" + games.size());
    }

    private Pool getPool(Long poolId, Map<Long, Pool> cache) {
        Pool pool = cache.get(poolId);
        if (pool != null) {
            return pool;
        }

        pool = poolRepository.findOne(poolId);
        if (pool == null) {
            throw new RuntimeException(String.format("Unable to find pool by ID %d", poolId));
        }

        cache.put(poolId, pool);

        return pool;
    }

    private int getWeek(List<GamePick> picks) {
        Set<Long> gameIds = picks.stream().map(GamePick::getGameId).collect(Collectors.toSet());

        Iterable<Game> allGames = gameRepository.findAll(gameIds);

        Set<Integer> weeks = StreamSupport.stream(allGames.spliterator(), false).map(Game::getWeek).collect(Collectors.toSet());
        if (weeks.size() != 1) {
            // Every game in the submission should have the same single week value.
            throw new RuntimeException(String.format("Expected only one week for the games provided in picks, found %d instead.", weeks.size()));
        }

        return weeks.iterator().next();
    }

    private int getSeason(List<GamePick> picks) {
        Set<Long> gameIds = picks.stream().map(GamePick::getGameId).collect(Collectors.toSet());

        Iterable<Game> allGames = gameRepository.findAll(gameIds);

        Set<Integer> seasons = StreamSupport.stream(allGames.spliterator(), false).map(Game::getSeason).collect(Collectors.toSet());
        if (seasons.size() != 1) {
            // Every game in the submission should have the same single season value.
            throw new RuntimeException(String.format("Expected only one season for the games provided in picks, found %d instead.", seasons.size()));
        }

        return seasons.iterator().next();
    }

    private void validatePicksValidForPool(Long poolId, List<GamePick> picks) {
        if (poolId == null) {
            throw new RuntimeException("No pool ID provided for pick submission request.");
        }

        if (CollectionUtils.isEmpty(picks)) {
            throw new RuntimeException("No picks provided for pick submission request.");
        }

        Pool pool = poolRepository.findOne(poolId);

        ScoringMethod method = ScoringMethod.getScoringMethodById(pool.getScoringMethod());
        if (method == null) {
            throw new RuntimeException(String.format("No scoring method found for pool with ID %d", poolId));
        }

        List<Integer> confidences = picks.stream().map(GamePick::getConfidence).collect(Collectors.toList());

        if (!method.areConfidencesValid(confidences)) {
            throw new RuntimeException(String.format("Invalid confidences provided for picks with pool using scoring method %s", method.getName()));
        }
    }

    void validateUserValidForPool(Long userId, Long poolId) {
        if (userId == null) {
            throw new RuntimeException("No user ID provided for pick submission request.");
        }

        if (poolId == null) {
            throw new RuntimeException("No pool ID provided for pick submission request.");
        }

        User user = userRepository.findOne(userId);

        if (user == null) {
            throw new RuntimeException(String.format("User with ID %d not found.", userId));
        }

        if (CollectionUtils.isEmpty(user.getUserPools())) {
            throw new RuntimeException(String.format("User with ID %d doesn't belong to any pools.", userId));
        }

        Set<Long> poolIds = user.getUserPools().stream().map(UserPool::getPoolId).collect(Collectors.toSet());

        if (!poolIds.contains(poolId)) {
            throw new RuntimeException(String.format("User with ID %d does not belong to pool with ID %d", userId, poolId));
        }
    }

    /**
     * Ensure that the correct number of games are provided in the picks and that all picks
     * span just one week.
     */
    void validateGames(List<GamePick> picks) {
        Set<Long> gameIds = picks.stream().map(GamePick::getGameId).collect(Collectors.toSet());

        if (gameIds.size() != picks.size()) {
            // We apparently doubled up on a game somehow.
            throw new RuntimeException(String.format("Game picks provided should have covered %d games, but only covered %d.", picks.size(), gameIds.size()));
        }

        int week = getWeek(picks);
        int season = getSeason(picks);

        List<Game> gamesForWeek = gameRepository.findAllBySeasonAndWeek(season, week);
        if (gamesForWeek.size() != picks.size()) {
            // Always expect a number of picks objects equal to the number of games in the week, even if some confidences are left empty for now.
            throw new RuntimeException(String.format("Unexpected number of picks provided. Expected %d, but received %d", gamesForWeek.size(), picks.size()));
        }

        // Validate that their picks match the games (can't pick the Vikings to win a game between the Chiefs and Steelers!)
        Map<Long, Game> gamesMap = new HashMap<>();
        for (Game game : gamesForWeek) {
            gamesMap.put(game.getGameId(), game);
        }

        for (GamePick gamePick : picks) {
            if (gamePick.getChosenTeamId() == null) {
                // User hasn't specified a pick for this game. That's fine.
                continue;
            }

            Game theGame = gamesMap.get(gamePick.getGameId());
            if (theGame == null) {
                throw new RuntimeException(String.format("Received pick for game ID %d which isn't valid for game with week ID %d", gamePick.getGameId(), week));
            }

            if (!gamePick.getChosenTeamId().equals(theGame.getHomeTeamId()) && !gamePick.getChosenTeamId().equals(theGame.getAwayTeamId())) {
                throw new RuntimeException(String.format("User has selected team with ID %d in a game between teams with IDs %d and %d.", gamePick.getChosenTeamId(), theGame.getAwayTeamId(), theGame.getHomeTeamId()));
            }
        }
    }
}
