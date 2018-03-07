package com.gci.pickem.data;

import com.gci.pickem.model.PoolView;

import javax.persistence.*;

@Entity
@Table(name = "pool")
public class Pool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pool_id")
    private long poolId;

    @Column(name = "pool_name")
    private String poolName;

    @Column(name = "scoring_method")
    private Integer scoringMethod;

    public Pool() {
    }

    public Pool(PoolView poolView) {
        this.poolName = poolView.getName();
        this.scoringMethod = poolView.getScoringMethod();
    }

    public long getPoolId() {
        return poolId;
    }

    public void setPoolId(long poolId) {
        this.poolId = poolId;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public Integer getScoringMethod() {
        return scoringMethod;
    }

    public void setScoringMethod(Integer scoringMethod) {
        this.scoringMethod = scoringMethod;
    }
}
