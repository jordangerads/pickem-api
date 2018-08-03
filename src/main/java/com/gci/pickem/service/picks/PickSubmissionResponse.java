package com.gci.pickem.service.picks;

import java.util.HashMap;
import java.util.Map;

public class PickSubmissionResponse {

    private boolean success;
    private Map<Long, PickInvalidityReason> pickInvalidities;

    public PickSubmissionResponse(Map<Long, PickInvalidityReason> pickInvalidities) {
        this.success = false;
        this.pickInvalidities = pickInvalidities;
    }

    public PickSubmissionResponse() {
        this.success = true;
        pickInvalidities = new HashMap<>();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Map<Long, PickInvalidityReason> getPickInvalidities() {
        return pickInvalidities;
    }

    public void setPickInvalidities(Map<Long, PickInvalidityReason> pickInvalidities) {
        this.pickInvalidities = pickInvalidities;
    }
}
