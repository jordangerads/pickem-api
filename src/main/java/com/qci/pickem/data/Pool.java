package com.qci.pickem.data;

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

    public Pool() {
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
}
