package com.gci.pickem.service.picks;

import com.gci.pickem.model.UserPicksRequest;

public interface PickService {

    void saveUserPicks(UserPicksRequest request);
}
