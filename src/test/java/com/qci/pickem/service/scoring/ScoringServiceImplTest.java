package com.qci.pickem.service.scoring;

import com.google.common.collect.Sets;
import com.qci.pickem.data.*;
import com.qci.pickem.repository.GameRepository;
import com.qci.pickem.repository.PickRepository;
import com.qci.pickem.repository.PoolRepository;
import com.qci.pickem.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScoringServiceImplTest {

    @Autowired private ScoringService scoringService;

    @Autowired private UserRepository userRepository;
    @Autowired private PickRepository pickRepository;
    @Autowired private GameRepository gameRepository;
    @Autowired private PoolRepository poolRepository;

    private long userId;
    private long poolId;
    private Set<Pick> picks;

    @Before
    public void setup() {

        Pool pool = new Pool();
        pool.setPoolName("Test Pool 1");

        pool = poolRepository.save(pool);

        poolId = pool.getPoolId();

        User user = new User();
        user.setFirstName("Jordan");
        user.setLastName("Test");

        user = userRepository.save(user);

        userId = user.getUserId();

        UserPool userPool = new UserPool();
        userPool.setUserId(userId);
        userPool.setPoolId(poolId);

        user.setUserPools(Sets.newHashSet(userPool));

        picks = createPicks(userId, poolId);
    }

    @Test
    @Transactional
    public void test() {
        int score = scoringService.getScore(userId, poolId, 1, 2017);

        assertEquals(136, score);
    }

    private Game createGame(long homeTeamId, long awayTeamId, long winningTeamId) {
        Game game = new Game();

        game.setWeek(1);
        game.setSeason(2017);
        game.setHomeTeamId(homeTeamId);
        game.setAwayTeamId(awayTeamId);
        game.setWinningTeamId(winningTeamId);
        game.setGameComplete(homeTeamId > 25);

        return gameRepository.save(game);
    }

    private Set<Pick> createPicks(long userId, long poolId) {
        Set<Pick> picks = new HashSet<>();

        for (int i = 0; i < 16; i++) {
            Game game = createGame(i, i + 16, i);

            Pick pick = new Pick();
            pick.setUserId(userId);
            pick.setPoolId(poolId);
            pick.setGameId(game.getGameId());
            pick.setChosenTeamId((long) i);
            pick.setConfidence(i);

            pick = pickRepository.save(pick);

            pick.setGame(game);

            picks.add(pick);
        }

        return picks;
    }
}