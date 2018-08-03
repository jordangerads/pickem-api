package com.gci.pickem.service.picks;

import com.gci.pickem.data.*;
import com.gci.pickem.model.GamePick;
import com.gci.pickem.model.UserPoolRole;
import com.gci.pickem.repository.GameRepository;
import com.gci.pickem.repository.PickRepository;
import com.gci.pickem.repository.PoolRepository;
import com.gci.pickem.repository.UserRepository;
import com.gci.pickem.service.mail.MailService;
import com.gci.pickem.service.mail.SendEmailRequest;
import com.gci.pickem.service.schedule.ScheduleService;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PickServiceImplTest {

    @Mock private PickRepository pickRepository;
    @Mock private GameRepository gameRepository;
    @Mock private PoolRepository poolRepository;
    @Mock private UserRepository userRepository;
    @Mock private ScheduleService scheduleService;
    @Mock private MailService mailService;

    private PickServiceImpl service;

    private Map<Long, User> userMap = new HashMap<>();
    private Map<Long, Game> gameMap = new HashMap<>();
    private Map<Long, Pool> poolMap = new HashMap<>();

    private List<SendEmailRequest> emailRequests = null;

    @Before
    public void setup() {
        setupUserMocks();
        setupGamesMocks();
        setupPoolMocks();

        doAnswer(invocation -> {
            emailRequests = invocation.getArgumentAt(0, List.class);
            return null;
        }).when(mailService).sendEmails(anyListOf(SendEmailRequest.class));

        service = new PickServiceImpl(pickRepository, gameRepository, poolRepository, userRepository, scheduleService, mailService);
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

    @Test
    public void testNotifyUsersWithoutPicksNoGamesOnDay() {
        LocalDate today = LocalDate.of(2018, 7, 27);
        service.notifyUsersWithoutPicks(today);
    }

    @Test
    public void testNotifyUsersWithoutPicks() {
        LocalDateTime first = LocalDateTime.of(2018, 7, 28, 0, 0, 0, 0);
        LocalDateTime second = LocalDateTime.of(2018, 7, 29, 0, 0, 0, 0);

        Map<Long, Game> mockGames = new HashMap<>();
        for (Game game : getMockGamesForDay()) {
            mockGames.put(game.getGameId(), game);
        }

        when(gameRepository.findByGameTimeEpochBetween(
                eq(first.atZone(ZoneId.of("America/New_York")).toInstant().toEpochMilli()),
                eq(second.atZone(ZoneId.of("America/New_York")).toInstant().toEpochMilli()))).thenReturn(Lists.newArrayList(mockGames.values()));

        when(gameRepository.findAll(anyCollectionOf(Long.class))).thenAnswer(invocation -> {
            Set<Long> gameIds = (Set<Long>) invocation.getArgumentAt(0, Collection.class);

            List<Game> games = new ArrayList<>();
            for (Long id : gameIds) {
                games.add(mockGames.get(id));
            }

            return games;
        });

        setupPickMocks();

        List<Long> gameIds = Lists.newArrayList(1L, 2L);
        when(userRepository.getUsersWithMissingPicks(eq(gameIds), eq(2))).thenAnswer(invocation -> {
            Set<Object[]> results = new HashSet<>();

            results.add(new Object[] { 4, 1 });
            results.add(new Object[] { 15, 1 });
            results.add(new Object[] { 16, 3 });

            return results;
        });

        LocalDate today = LocalDate.of(2018, 7, 28);
        service.notifyUsersWithoutPicks(today);

        Assert.assertEquals(3, emailRequests.size());

        // John is first.
        Optional<SendEmailRequest> optional = emailRequests.stream().filter(req -> "John".equals(req.getRecipientName())).findFirst();
        Assert.assertTrue(optional.isPresent());

        SendEmailRequest john = optional.get();
        Assert.assertEquals("John@Locke.com", john.getRecipientEmail());

        Map<String, Object> data = john.getRequestData();
        Assert.assertEquals(3, data.size());
        Assert.assertEquals("Test Pool 3", data.get("poolName"));
        Assert.assertEquals("John", data.get("firstName"));

        List<Map<String, Object>> missingGames = (List) data.get("missingGames");
        Assert.assertEquals(1, missingGames.size());

        Map<String, Object> game = missingGames.get(0);
        Assert.assertEquals("Vikings", game.get("awayTeamName"));
        Assert.assertEquals("Bears", game.get("homeTeamName"));
        Assert.assertEquals("2018-07-28T16:00:00Z", game.get("gameTime"));

        // Jack next.
        optional = emailRequests.stream().filter(req -> "Jack".equals(req.getRecipientName())).findFirst();
        Assert.assertTrue(optional.isPresent());

        SendEmailRequest jack = optional.get();
        Assert.assertEquals("Jack@Shepherd.com", jack.getRecipientEmail());

        data = jack.getRequestData();
        Assert.assertEquals(3, data.size());
        Assert.assertEquals("Test Pool 1", data.get("poolName"));
        Assert.assertEquals("Jack", data.get("firstName"));

        missingGames = (List) data.get("missingGames");
        Assert.assertEquals(2, missingGames.size());

        game = missingGames.stream().filter(pick -> "Packers".equals(pick.get("awayTeamName"))).findFirst().get();
        Assert.assertEquals("Packers", game.get("awayTeamName"));
        Assert.assertEquals("Lions", game.get("homeTeamName"));
        Assert.assertEquals("2018-07-28T21:00:00Z", game.get("gameTime"));

        game = missingGames.stream().filter(pick -> "Vikings".equals(pick.get("awayTeamName"))).findFirst().get();
        Assert.assertEquals("Vikings", game.get("awayTeamName"));
        Assert.assertEquals("Bears", game.get("homeTeamName"));
        Assert.assertEquals("2018-07-28T16:00:00Z", game.get("gameTime"));

        // Hugo last!
        optional = emailRequests.stream().filter(req -> "Hugo".equals(req.getRecipientName())).findFirst();
        Assert.assertTrue(optional.isPresent());

        SendEmailRequest hugo = optional.get();
        Assert.assertEquals("Hugo@Reyes.com", hugo.getRecipientEmail());

        data = hugo.getRequestData();
        Assert.assertEquals(3, data.size());
        Assert.assertEquals("Test Pool 1", data.get("poolName"));
        Assert.assertEquals("Hugo", data.get("firstName"));

        missingGames = (List) data.get("missingGames");
        Assert.assertEquals(1, missingGames.size());

        game = missingGames.get(0);
        Assert.assertEquals("Packers", game.get("awayTeamName"));
        Assert.assertEquals("Lions", game.get("homeTeamName"));
        Assert.assertEquals("2018-07-28T21:00:00Z", game.get("gameTime"));
    }

    private void setupPickMocks() {
        Map<Long, List<Pick>> picksByUser = new HashMap<>();

        // Picks for user ID 4. None for pool 1.
        picksByUser.put(4L, new ArrayList<>());

        // Picks for user ID 15. Has two picks, one is null
        picksByUser.put(15L,
            Lists.newArrayList(
                makePick(15L, 1L, 1L, 16),
                makePick(15L, 1L, 2L, null)));

        // Picks for user ID 16. Has one pick, missing the other.
        picksByUser.put(16L,
            Lists.newArrayList(
                makePick(16L, 3L, 2L, 16)));

        when(pickRepository.getPicks(anyLong(), anyLong(), eq(2018), eq(1))).thenAnswer(invocation -> {
            Long userId = invocation.getArgumentAt(0, Long.class);
            Long poolId = invocation.getArgumentAt(1, Long.class);

            List<Pick> userPicks = picksByUser.get(userId);

            return userPicks.stream().filter(pick -> pick.getPoolId().equals(poolId)).collect(Collectors.toSet());
        });
    }

    private Pick makePick(Long userId, Long poolId, Long gameId, Integer confidence) {
        Pick pick = new Pick();

        pick.setUserId(userId);
        pick.setPoolId(poolId);
        pick.setGameId(gameId);
        pick.setConfidence(confidence);

        return pick;
    }

    private List<Game> getMockGamesForDay() {
        Game first = new Game();
        first.setAwayTeamId(1L);

        first.setAwayTeam(new Team());
        first.getAwayTeam().setTeamName("Vikings");

        first.setHomeTeam(new Team());
        first.getHomeTeam().setTeamName("Bears");

        LocalDateTime firstTime = LocalDateTime.of(2018, 7, 28, 12, 0, 0, 0);
        first.setGameTimeEpoch(firstTime.atZone(ZoneId.of("America/New_York")).toInstant().getEpochSecond());

        first.setHomeTeamId(2L);
        first.setGameId(1L);
        first.setWeek(1);
        first.setSeason(2018);

        Game second = new Game();

        second.setAwayTeam(new Team());
        second.getAwayTeam().setTeamName("Packers");

        second.setHomeTeam(new Team());
        second.getHomeTeam().setTeamName("Lions");

        LocalDateTime secondTime = LocalDateTime.of(2018, 7, 28, 17, 0, 0, 0);
        second.setGameTimeEpoch(secondTime.atZone(ZoneId.of("America/New_York")).toInstant().getEpochSecond());

        second.setAwayTeamId(3L);
        second.setHomeTeamId(4L);
        second.setGameId(2L);
        second.setWeek(1);
        second.setSeason(2018);

        return Lists.newArrayList(first, second);
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
        userMap.put(8L, createUser("Kate", "Austin", 8L, ImmutableMap.of()));
        userMap.put(15L, createUser("Hugo", "Reyes", 15L, ImmutableMap.of(1L, "Test Pool 1")));
        userMap.put(16L, createUser("John", "Locke", 16L, ImmutableMap.of(3L, "Test Pool 3", 4L, "Test Pool 4")));

        when(userRepository.findOne(anyLong())).thenAnswer(invocation -> userMap.get(invocation.getArgumentAt(0, Long.class)));
    }

    private void setupPoolMocks() {
        poolMap.put(1L, createPool(1L));
        poolMap.put(2L, createPool(2L));
        poolMap.put(3L, createPool(3L));
        poolMap.put(4L, createPool(4L));

        when(poolRepository.findOne(anyLong())).thenAnswer(invocation -> poolMap.get(invocation.getArgumentAt(0, Long.class)));
    }

    private Pool createPool(Long id) {
        Pool pool = new Pool();

        pool.setPoolId(id);
        pool.setPoolName(String.format("Test Pool %d", id));
        pool.setScoringMethod(1);

        return pool;
    }

    private User createUser(String fn, String ln, Long id, Map<Long, String> pools) {
        User user = new User();
        user.setFirstName(fn);
        user.setLastName(ln);
        user.setUserId(id);
        user.setEmail(String.format("%s@%s.com", fn, ln));

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
