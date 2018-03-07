package com.gci.pickem.service.scoring;

import com.gci.pickem.data.Game;
import com.gci.pickem.data.Pick;
import com.gci.pickem.data.User;
import com.gci.pickem.data.UserPool;
import com.gci.pickem.exception.InvalidScoringRequestException;
import com.gci.pickem.exception.InvalidUserPoolException;
import com.gci.pickem.exception.MissingRequiredDataException;
import com.gci.pickem.exception.UserNotFoundException;
import com.gci.pickem.repository.PickRepository;
import com.gci.pickem.repository.UserRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScoringServiceImpl implements ScoringService {
    private static final Logger log = LoggerFactory.getLogger(ScoringServiceImpl.class);

    private UserRepository userRepository;
    private PickRepository pickRepository;

    @Autowired
    ScoringServiceImpl(
        UserRepository userRepository,
        PickRepository pickRepository
    ) {
        this.userRepository = userRepository;
        this.pickRepository = pickRepository;
    }

    @Override
    public int getScore(long userId, long poolId, int week, int season) {
        // Get the user.
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new UserNotFoundException(String.format("No user exists with ID %d", userId));
        }

        // Verify user is a member of the specified pool.
        Set<Long> userPoolIds = user.getUserPools().stream().map(UserPool::getPoolId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userPoolIds) || !userPoolIds.contains(poolId)) {
            throw new InvalidUserPoolException(String.format("User with ID %d does not belong to pool with ID %d", userId, poolId));
        }

        // Retrieve the user's picks for the week (and this pool!)
        Set<Pick> picks = pickRepository.getPicks(userId, poolId, season, week);

        return getScoreForPicks(picks);
    }

    int getScoreForPicks(Set<Pick> picks) {
        // For each pick, check against the pool's strategy to see how many picks the user got right and calculate the score.
        int score = 0;

        for (Pick pick : picks) {
            try {
                score += getScoreForPick(pick);
            } catch (InvalidScoringRequestException e) {
                // This is okay, the game just isn't complete yet.
                log.trace("", e);
            }
        }

        return score;
    }

    private int getScoreForPick(Pick pick) {
        if (pick == null) {
            throw new MissingRequiredDataException("Pick must not be null in order to calculate score");
        }

        if (pick.getGame() == null) {
            throw new MissingRequiredDataException(String.format("No associated game found for pick with ID %d", pick.getPickId()));
        }

        Game game = pick.getGame();
        if (!game.getGameComplete()) {
            throw new InvalidScoringRequestException(String.format("Game with ID %d is not complete. Unable to calculate score.", game.getGameId()));
        }

        if (pick.getChosenTeamId() == null || !pick.getChosenTeamId().equals(game.getWinningTeamId())) {
            // User didn't make a pick or the pick was wrong. Goose egg for you.
            return 0;
        } else {
            return pick.getConfidence();
        }
    }
}
