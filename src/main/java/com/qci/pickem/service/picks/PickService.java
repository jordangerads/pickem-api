package com.qci.pickem.service.picks;

import com.qci.pickem.model.UserPicksRequest;

public interface PickService {

    void saveUserPicks(UserPicksRequest request);
}
