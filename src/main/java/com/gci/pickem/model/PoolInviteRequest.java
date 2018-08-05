package com.gci.pickem.model;

import java.util.List;

public class PoolInviteRequest {

    private long poolId;
    private List<String> inviteeEmails;
    private String clientUrl;

    public long getPoolId() {
        return poolId;
    }

    public void setPoolId(long poolId) {
        this.poolId = poolId;
    }

    public List<String> getInviteeEmails() {
        return inviteeEmails;
    }

    public void setInviteeEmails(List<String> inviteeEmails) {
        this.inviteeEmails = inviteeEmails;
    }

    public String getClientUrl() {
        return clientUrl;
    }

    public void setClientUrl(String clientUrl) {
        this.clientUrl = clientUrl;
    }
}
