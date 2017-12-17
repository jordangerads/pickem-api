package com.qci.pickem.service.mysportsfeeds;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qci.pickem.model.mysportsfeeds.FullGameSchedule;
import com.qci.pickem.model.mysportsfeeds.FullGameScheduleResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Calendar;

@Service
public class MySportsFeedsServiceImpl implements MySportsFeedsService {
    private static final Logger log = LoggerFactory.getLogger(MySportsFeedsServiceImpl.class);

    @Value("${mysportsfeed.username}")
    private String username;

    @Value("${mysportsfeed.password}")
    private String password;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public FullGameSchedule getGamesForSeasonAndWeek(int season, int week) {
        // If the request was for any year OTHER than the current year, assume they meant the year-to-(year+1) season!
        String requestYear =
            Calendar.getInstance().get(Calendar.YEAR) == season ?
                "current" :
                String.format("%d-%d-regular", season, season + 1);

        String url = String.format("https://api.mysportsfeeds.com/v1.1/pull/nfl/%s/full_game_schedule.json", requestYear);

        FullGameScheduleResponse response = getResponse(url, FullGameScheduleResponse.class);
        if (response == null) {
            throw new RuntimeException(
                String.format("No response retrieved for full game schedule request for season %d and week %d", season, week));
        }

        return response.getFullGameSchedule();
    }

    private <T> T getResponse(String requestUrl, Class<T> responseClass) {
        try {
            URL url = new URL(requestUrl);
            String encoding = Base64.getEncoder().encodeToString(String.format("%s:%s", username, password).getBytes());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty  ("Authorization", "Basic " + encoding);

            String value = IOUtils.toString(connection.getInputStream(), "UTF-8");

            return MAPPER.readValue(value, responseClass);
        } catch (Exception e) {
            log.trace("", e);
            log.error("Unable to get successful response for request URL %s: %s", requestUrl, e.getMessage());
        }

        return null;
    }
}
