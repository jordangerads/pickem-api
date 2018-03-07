package com.gci.pickem.service.pool;

import com.gci.pickem.model.PoolView;

public interface PoolService {

    PoolView createPool(long userId, PoolView poolView);
}
