package com.gci.pickem.job;

import com.gci.pickem.service.picks.PickService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MissingPickNotifier {
    private static final Logger log = LoggerFactory.getLogger(MissingPickNotifier.class);

    private PickService pickService;

    @Autowired
    MissingPickNotifier(PickService pickService) {
        this.pickService = pickService;
    }

    // Run at 2am Eastern time every day, after schedule updates have been processed.
    @Scheduled(cron = "0 0 2 * * *", zone = "America/New_York")
    public void notifyUsers() {
        log.info("Executing notifyUsers process.");

        // Don't pass a date, use today.
        pickService.notifyUsersWithoutPicks(null);

        log.info("notifyUsers process complete.");
    }
}
