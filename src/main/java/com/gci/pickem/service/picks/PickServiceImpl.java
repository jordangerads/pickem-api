package com.gci.pickem.service.picks;

import com.gci.pickem.data.*;
import com.gci.pickem.model.GamePick;
import com.gci.pickem.model.UserPicksRequest;
import com.gci.pickem.repository.GameRepository;
import com.gci.pickem.repository.UserRepository;
import com.gci.pickem.model.ScoringMethod;
import com.gci.pickem.repository.PickRepository;
import com.gci.pickem.repository.PoolRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PickServiceImpl implements PickService {
    private static final Logger log = LoggerFactory.getLogger(PickServiceImpl.class);

    private PickRepository pickRepository;
    private GameRepository gameRepository;
    private PoolRepository poolRepository;
    private UserRepository userRepository;

    @Autowired
    PickServiceImpl(
        PickRepository pickRepository,
        GameRepository gameRepository,
        PoolRepository poolRepository,
        UserRepository userRepository
    ) {
        this.pickRepository = pickRepository;
        this.gameRepository = gameRepository;
        this.poolRepository = poolRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveUserPicks(UserPicksRequest request) {
        if (request == null || request.getUserId() == null || request.getPoolId() == null ||
            CollectionUtils.isEmpty(request.getGamePicks())) {
            log.info("Insufficient information received to record picks for user.");
            return;
        }

        validateUserValidForPool(request.getUserId(), request.getPoolId());
        validateGames(request.getGamePicks());
        validatePicksValidForPool(request.getPoolId(), request.getGamePicks());

        // Everything appears to be valid. Let's save them picks!
        List<Pick> picks =
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

        pickRepository.save(picks);
    }

    void validatePicksValidForPool(Long poolId, List<GamePick> picks) {
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

        Iterable<Game> allGames = gameRepository.findAll(gameIds);

        Set<Integer> weeks = StreamSupport.stream(allGames.spliterator(), false).map(Game::getWeek).collect(Collectors.toSet());

        if (weeks.size() != 1) {
            // Every game in the submission should have the same single week value.
            throw new RuntimeException(String.format("Expected only one week for the games provided in picks, found %d instead.", weeks.size()));
        }

        Set<Integer> seasons = StreamSupport.stream(allGames.spliterator(), false).map(Game::getSeason).collect(Collectors.toSet());

        if (seasons.size() != 1) {
            // Every game in the submission should have the same single season value.
            throw new RuntimeException(String.format("Expected only one season for the games provided in picks, found %d instead.", seasons.size()));
        }

        List<Game> gamesForWeek = gameRepository.findAllBySeasonAndWeek(seasons.iterator().next(), weeks.iterator().next());
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
                throw new RuntimeException(String.format("Received pick for game ID %d which isn't valid for game with week ID %d", gamePick.getGameId(), weeks.iterator().next()));
            }

            if (!gamePick.getChosenTeamId().equals(theGame.getHomeTeamId()) && !gamePick.getChosenTeamId().equals(theGame.getAwayTeamId())) {
                throw new RuntimeException(String.format("User has selected team with ID %d in a game between teams with IDs %d and %d.", gamePick.getChosenTeamId(), theGame.getAwayTeamId(), theGame.getHomeTeamId()));
            }
        }
    }
}
