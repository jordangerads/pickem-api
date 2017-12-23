package com.qci.pickem.model;

import com.qci.pickem.data.Pool;

// Consider things like max size, etc in the future.
public class PoolView {

    private String name;
    private Integer scoringMethod;

    public PoolView() {
    }

    public PoolView(Pool pool) {
        this.name = pool.getPoolName();
        this.scoringMethod = pool.getScoringMethod();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getScoringMethod() {
        return scoringMethod;
    }

    public void setScoringMethod(Integer scoringMethod) {
        this.scoringMethod = scoringMethod;
    }
}
