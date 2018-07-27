package com.gci.pickem.service.mail;

public enum MailType {
    PICKS_REMINDER("tem_hqtMmttPpSQJTg668PHkD4dP");

    private final String templateId;

    private MailType(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateId() {
        return templateId;
    }
}
