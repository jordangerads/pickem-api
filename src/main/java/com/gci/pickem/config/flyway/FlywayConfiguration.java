package com.gci.pickem.config.flyway;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/*
  Package private so no one can get access to it.
 */
@Component
class FlywayConfiguration {
    private static final Logger log = LoggerFactory.getLogger(FlywayConfiguration.class);

    @Autowired
    FlywayConfiguration(
        DataSource dataSource
    ) {
        log.info("Performing DB migration process.");

        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();

        log.info("DB migration process complete.");
    }
}
