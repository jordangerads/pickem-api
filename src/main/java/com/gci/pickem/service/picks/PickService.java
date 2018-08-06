package com.gci.pickem.service.picks;

import com.gci.pickem.model.GamePick;
import com.gci.pickem.model.UserPicksRequest;

import java.time.LocalDate;
import java.util.List;

public interface PickService {

    PickSubmissionResponse saveUserPicks(long userId, UserPicksRequest request);

    List<GamePick> getUserPicks(long userId, long poolId, int season, int week);

    List<Integer> getConfidenceValues(long poolId, int season, int week);

    void notifyUsersWithoutPicks(LocalDate date);
}
