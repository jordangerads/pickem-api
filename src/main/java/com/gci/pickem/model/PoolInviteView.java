package com.gci.pickem.model;

import com.gci.pickem.data.PoolInvite;

public class PoolInviteView {

    private String email;
    private String status;
    private String poolName;
    private Long poolId;

    public PoolInviteView(PoolInvite poolInvite) {
        this.email = poolInvite.getInviteeEmail();
        this.status = poolInvite.getInviteStatus();
        this.poolName = poolInvite.getPool().getPoolName();
        this.poolId = poolInvite.getPoolId();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public Long getPoolId() {
        return poolId;
    }

    public void setPoolId(Long poolId) {
        this.poolId = poolId;
    }
}
