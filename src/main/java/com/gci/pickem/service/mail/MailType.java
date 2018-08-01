package com.gci.pickem.service.mail;

public enum MailType {
    PICKS_REMINDER("tem_hqtMmttPpSQJTg668PHkD4dP"),
    USER_REGISTRATION("tem_RpFRwRCq8FwPhqTCcpCtPHtP"),
    FORGOT_PASSWORD("tem_Cqv8dTFKppBcx63qFmGKj9RT");

    private final String templateId;

    private MailType(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateId() {
        return templateId;
    }
}
