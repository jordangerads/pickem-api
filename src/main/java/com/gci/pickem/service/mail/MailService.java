package com.gci.pickem.service.mail;

import java.util.Collection;

public interface MailService {

    void sendEmails(Collection<SendEmailRequest> requests);

    void sendEmail(SendEmailRequest request);
}
