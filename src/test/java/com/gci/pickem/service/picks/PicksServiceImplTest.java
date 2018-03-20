package com.gci.pickem.service.picks;

import com.gci.pickem.data.Game;
import com.gci.pickem.data.Pool;
import com.gci.pickem.data.User;
import com.gci.pickem.model.GamePick;
import com.gci.pickem.repository.GameRepository;
import com.gci.pickem.repository.UserRepository;
import com.gci.pickem.service.mysportsfeeds.MySportsFeedsService;
import com.google.common.collect.ImmutableMap;
import com.gci.pickem.data.UserPool;
import com.gci.pickem.model.UserPoolRole;
import com.gci.pickem.repository.PickRepository;
import com.gci.pickem.repository.PoolRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PicksServiceImplTest {

    @Mock private PickRepository pickRepository;
    @Mock private GameRepository gameRepository;
    @Mock private PoolRepository poolRepository;
    @Mock private UserRepository userRepository;
    @Mock private MySportsFeedsService mySportsFeedsService;

    private PickServiceImpl service;

    private Map<Long, User> userMap = new HashMap<>();
    private Map<Long, Game> gameMap = new HashMap<>();

    @Before
    public void setup() {
        setupUserMocks();
        setupGamesMocks();

        service = new PickServiceImpl(pickRepository, gameRepository, poolRepository, userRepository, mySportsFeedsService);
    }

    @Test(expected = RuntimeException.class)
    public void testValidateUserForPoolNullPoolId() {
        service.validateUserValidForPool(4L, null);
    }

    @Test(expected = RuntimeException.class)
    public void testValidateUserForPoolNullUserId() {
        service.validateUserValidForPool(null, 4L);
    }

    @Test(expected = RuntimeException.class)
    public void testValidateUserForPoolNoUserFound() {
        service.validateUserValidForPool(8L, 10L);
    }

    @Test(expected = RuntimeException.class)
    public void testValidateUserForPoolNoPoolsForUser() {
        service.validateUserValidForPool(15L, 10L);
    }

    @Test(expected = RuntimeException.class)
    public void testValidateUserForPoolUserNotInPool() {
        service.validateUserValidForPool(4L, 8L);
    }

    @Test
    public void testValidateUserForPool() {
        service.validateUserValidForPool(4L, 1L);
    }

    @Test(expected = RuntimeException.class)
    public void testValidateGamesMultipleOfSameGame() {
        List<GamePick> picks = new ArrayList<>();

        picks.add(getGamePick(1L, 1L, 16));
        picks.add(getGamePick(1L, 1L, 15));

        service.validateGames(picks);
    }

    @Test(expected = RuntimeException.class)
    public void testValidateGamesMultipleWeeks() {
        List<GamePick> picks = new ArrayList<>();

        picks.add(getGamePick(1L, 1L, 16));
        picks.add(getGamePick(17L, 1L, 15));

        service.validateGames(picks);
    }

    @Test(expected = RuntimeException.class)
    public void testValidateGamesIncompletePicksForWeek() {
        List<GamePick> picks = new ArrayList<>();

        picks.add(getGamePick(1L, 1L, 16));
        picks.add(getGamePick(2L, 2L, 15));

        service.validateGames(picks);
    }

    @Test(expected = RuntimeException.class)
    public void testValidateGamesInvalidPickForGame() {
        List<GamePick> picks = new ArrayList<>();

        picks.add(getGamePick(1L, 1L, 16));
        picks.add(getGamePick(2L, 2L, 15));
        picks.add(getGamePick(3L, 3L, 14));
        picks.add(getGamePick(4L, 4L, 13));
        picks.add(getGamePick(5L, 5L, 12));
        picks.add(getGamePick(6L, 6L, 11));
        picks.add(getGamePick(7L, 7L, 10));
        picks.add(getGamePick(8L, 8L, 9));
        picks.add(getGamePick(9L, 9L, 8));
        picks.add(getGamePick(10L, 10L, 7));
        picks.add(getGamePick(11L, 11L, 6));
        picks.add(getGamePick(12L, 12L, 5));
        picks.add(getGamePick(13L, 13L, 4));
        picks.add(getGamePick(14L, 14L, 3));
        picks.add(getGamePick(15L, null, 2)); // throw in a non-pick
        picks.add(getGamePick(16L, 1L, 1));

        service.validateGames(picks);
    }

    @Test(expected = RuntimeException.class)
    public void testValidateGamesInvalidPickForWeek() {
        List<GamePick> picks = new ArrayList<>();

        picks.add(getGamePick(1L, 1L, 16));
        picks.add(getGamePick(2L, 2L, 15));
        picks.add(getGamePick(3L, 3L, 14));
        picks.add(getGamePick(4L, 4L, 13));
        picks.add(getGamePick(5L, 5L, 12));
        picks.add(getGamePick(6L, 6L, 11));
        picks.add(getGamePick(7L, 7L, 10));
        picks.add(getGamePick(8L, 8L, 9));
        picks.add(getGamePick(9L, 9L, 8));
        picks.add(getGamePick(10L, 10L, 7));
        picks.add(getGamePick(11L, 11L, 6));
        picks.add(getGamePick(12L, 12L, 5));
        picks.add(getGamePick(13L, 13L, 4));
        picks.add(getGamePick(14L, 14L, 3));
        picks.add(getGamePick(15L, 15L, 2));
        picks.add(getGamePick(null, 1L, 1));

        service.validateGames(picks);
    }

    private void setupGamesMocks() {
        for (long i = 1; i <= 16; i++) {
            gameMap.put(i, createGame(i, 1, i, i + 16, true));
        }

        for (long i = 17; i <= 32; i++) {
            // 1 - 32: 17 - 16 = 1, 33 - i + 16) = 32
            // 2 - 31: 18 - 16 = 2, 33 - i + 16) = 31
            gameMap.put(i, createGame(i, 2, i - 16, 49 - i , true));
        }

        when(gameRepository.findAll()).thenAnswer(invocation -> gameMap.values());

        when(gameRepository.findAll(anyCollectionOf(Long.class))).thenAnswer(invocation -> {
            Collection<Long> gameIds = invocation.getArgumentAt(0, Set.class);

            List<Game> games = new ArrayList<>();
            gameIds.forEach(id -> {
                Game game = gameMap.get(id);
                if (game != null) {
                    games.add(game);
                }
            });

            return games;
        });

        when(gameRepository.findAllBySeasonAndWeek(anyInt(), anyInt())).thenAnswer(invocation ->
            gameMap.values()
                .stream()
                .filter(game ->
                    game.getSeason().equals(invocation.getArgumentAt(0, Integer.class)) &&
                    game.getWeek().equals(invocation.getArgumentAt(1, Integer.class))
                )
                .collect(Collectors.toList()));
    }

    private Game createGame(Long id, Integer week, Long home, Long away, Boolean gameComplete) {
        Game game = new Game();

        game.setGameId(id);
        game.setWeek(week);
        game.setHomeTeamId(home);
        game.setAwayTeamId(away);
        game.setSeason(2018);
        game.setGameComplete(gameComplete);

        return game;
    }

    private GamePick getGamePick(Long gameId, Long teamId, Integer confidence) {
        GamePick gamePick = new GamePick();

        gamePick.setGameId(gameId);
        gamePick.setChosenTeamId(teamId);
        gamePick.setConfidence(confidence);

        return gamePick;
    }

    private void setupUserMocks() {
        userMap.put(4L, createUser("Jack", "Shepherd", 4L, ImmutableMap.of(1L, "Test Pool 1", 2L, "Test Pool 2")));
        userMap.put(8L, createUser("Kate", "Austin", 15L, ImmutableMap.of()));

        when(userRepository.findOne(anyLong())).thenAnswer(invocation -> userMap.get(invocation.getArgumentAt(0, Long.class)));
    }

    private User createUser(String fn, String ln, Long id, Map<Long, String> pools) {
        User user = new User();
        user.setFirstName(fn);
        user.setLastName(ln);
        user.setUserId(id);

        Set<UserPool> userPools = new HashSet<>();

        for (Map.Entry<Long, String> entry : pools.entrySet()) {
            Pool pool = new Pool();
            pool.setPoolId(entry.getKey());
            pool.setPoolName(entry.getValue());
            pool.setScoringMethod((int) (entry.getKey() % 2) + 1);

            UserPool userPool = new UserPool();
            userPool.setUser(user);
            userPool.setPool(pool);
            userPool.setPoolId(pool.getPoolId());
            userPool.setUserId(user.getUserId());
            userPool.setUserRole(UserPoolRole.values()[(int) (entry.getKey() % 2)].name());
            userPool.setUserPoolId(user.getUserId() * 100 + pool.getPoolId());

            userPools.add(userPool);
        }

        user.setUserPools(userPools);

        return user;
    }
}
