package com.gci.pickem.service.mail;

import com.google.common.collect.ImmutableMap;
import com.sendwithus.SendWithUs;
import com.sendwithus.SendWithUsSendRequest;
import com.sendwithus.exception.SendWithUsException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

@Service
public class MailServiceImpl implements MailService {
    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    @Value("${sendwithus.api-key}")
    private String apiKey;

    private ExecutorService executorService;

    private SendWithUs client;

    @Autowired
    MailServiceImpl(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @PostConstruct
    public void postConstruct() {
        client = new SendWithUs(apiKey);
    }

    @Override
    @Async
    public void sendEmails(Collection<SendEmailRequest> requests) {
        for (SendEmailRequest request : requests) {
            sendEmail(request);
        }
    }

    @Override
    @Async
    public void sendEmail(SendEmailRequest request) {
        SendWithUsSendRequest swuRequest = new SendWithUsSendRequest();

        swuRequest.setEmailId(request.getTemplateId());

        if (StringUtils.isBlank(request.getRecipientName())) {
            swuRequest.setRecipient(ImmutableMap.of("address", request.getRecipientEmail()));
        } else {
            swuRequest.setRecipient(ImmutableMap.of("name", request.getRecipientName(), "address", request.getRecipientEmail()));
        }

        swuRequest.setEmailData(request.getRequestData());

        executorService.submit(new SendEmailRunnable(swuRequest));
    }

    private class SendEmailRunnable implements Runnable {
        private final SendWithUsSendRequest request;

        SendEmailRunnable(SendWithUsSendRequest request) {
            this.request = request;
        }

        @Override
        public void run() {
            try {
                client.send(request);
            } catch (SendWithUsException e) {
                log.error("{}", e);
            }
        }
    }
}
