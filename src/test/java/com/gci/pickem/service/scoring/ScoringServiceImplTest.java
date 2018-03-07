package com.gci.pickem.service.scoring;

import com.gci.pickem.data.*;
import com.gci.pickem.repository.GameRepository;
import com.gci.pickem.repository.UserRepository;
import com.google.common.collect.Sets;
import com.gci.pickem.repository.PickRepository;
import com.gci.pickem.repository.PoolRepository;
import org.junit.Before;
import org.junit.Ignore;
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

    @Autowired private ScoringServiceImpl scoringService;

    @Autowired private UserRepository userRepository;
    @Autowired private PickRepository pickRepository;
    @Autowired private GameRepository gameRepository;
    @Autowired private PoolRepository poolRepository;

    private Set<Pick> picks;

    @Before
    public void setup() {

        Pool pool = new Pool();
        pool.setPoolName("Test Pool 1");

        pool = poolRepository.save(pool);

        long poolId = pool.getPoolId();

        User user = new User();
        user.setFirstName("Jordan");
        user.setLastName("Test");

        user = userRepository.save(user);

        long userId = user.getUserId();

        UserPool userPool = new UserPool();
        userPool.setUserId(userId);
        userPool.setPoolId(poolId);

        user.setUserPools(Sets.newHashSet(userPool));

        picks = createPicks(userId, poolId);
    }

    @Test
    @Ignore // Until h2 is up and running.
    @Transactional
    public void test() {
        int score = scoringService.getScoreForPicks(picks);

        // 13, 14, 15 are not complete yet, so scores of 14, 15, and 16 aren't in. 136 is the max minus 45 = 91
        assertEquals(91, score);
    }

    private Game createGame(long homeTeamId, long awayTeamId, long winningTeamId) {
        Game game = new Game();

        game.setWeek(1);
        game.setSeason(2017);
        game.setHomeTeamId(homeTeamId);
        game.setAwayTeamId(awayTeamId);
        game.setWinningTeamId(winningTeamId);
        game.setGameComplete(homeTeamId < 13); // make most of the games complete.

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
            pick.setConfidence(i + 1); // scores start at 1 and go to 16.

            pick = pickRepository.save(pick);

            pick.setGame(game);

            picks.add(pick);
        }

        return picks;
    }
}