package com.gci.pickem.model;

import java.util.List;

public class PoolInviteResponse {

    private final List<String> errorEmails;

    public PoolInviteResponse(List<String> errorEmails) {
        this.errorEmails = errorEmails;
    }

    public List<String> getErrorEmails() {
        return errorEmails;
    }
}
