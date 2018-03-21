package com.gci.pickem.service.picks;

import com.gci.pickem.model.UserPicksRequest;

import java.util.List;

public interface PickService {

    void saveUserPicks(UserPicksRequest request);

    List<Integer> getConfidenceValues(Long poolId, Integer season, Integer week);
}
