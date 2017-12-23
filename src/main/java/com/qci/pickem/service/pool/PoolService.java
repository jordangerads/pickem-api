package com.qci.pickem.service.pool;

import com.qci.pickem.model.PoolView;

public interface PoolService {

    PoolView createPool(long userId, PoolView poolView);
}
