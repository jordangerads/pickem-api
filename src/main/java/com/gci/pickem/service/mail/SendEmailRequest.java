package com.gci.pickem.service.mail;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class SendEmailRequest {
    private String recipientName;
    private String recipientEmail;
    private String templateId;
    private Map<String, Object> requestData = new HashMap<>();

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Map<String, Object> getRequestData() {
        // Don't let this get modified, this is view only.
        return ImmutableMap.copyOf(requestData);
    }

    public void addRequestData(String key, Object value) {
        requestData.put(key, value);
    }
}
