package com.gci.pickem.service.mail;

public enum MailType {
    PICKS_REMINDER("tem_hqtMmttPpSQJTg668PHkD4dP"),
    USER_REGISTRATION("tem_RpFRwRCq8FwPhqTCcpCtPHtP"),
    FORGOT_PASSWORD("tem_Cqv8dTFKppBcx63qFmGKj9RT"),
    ADMIN_POOL_MESSAGE(""),
    POOL_INVITE("tem_jbHYWHKyJQmC3qbXpVyRkH3B");

    private final String templateId;

    MailType(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateId() {
        return templateId;
    }
}
