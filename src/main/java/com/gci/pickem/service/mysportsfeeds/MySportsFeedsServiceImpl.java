package com.gci.pickem.service.mysportsfeeds;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gci.pickem.model.mysportsfeeds.FullGameSchedule;
import com.gci.pickem.model.mysportsfeeds.FullGameScheduleResponse;
import com.gci.pickem.model.mysportsfeeds.GameScore;
import com.gci.pickem.model.mysportsfeeds.ScoreboardResponse;
import com.gci.pickem.util.SeasonUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Calendar;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class MySportsFeedsServiceImpl implements MySportsFeedsService {
    private static final Logger log = LoggerFactory.getLogger(MySportsFeedsServiceImpl.class);

    @Value("${mysportsfeeds.username}")
    private String username;

    @Value("${mysportsfeeds.password}")
    private String password;

    @Value("${mysportsfeeds.url.base}")
    private String baseUrl;

    @Value("${mysportsfeeds.version.current}")
    private String apiVersion;

    @Value("${mysportsfeeds.format}")
    private String dataFormat;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final CacheLoader<String, String> cacheLoader = new CacheLoader<String, String>() {
        @Override
        @ParametersAreNonnullByDefault
        public String load(String key) throws Exception {
            return getRawResponse(key);
        }

        private String getRawResponse(String requestUrl) {
            try {
                URL url = new URL(requestUrl);
                String encoding = Base64.getEncoder().encodeToString(String.format("%s:%s", username, password).getBytes());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setRequestProperty  ("Authorization", "Basic " + encoding);

                return IOUtils.toString(connection.getInputStream(), "UTF-8");
            } catch (Exception e) {
                log.trace("", e);
                log.error(String.format("Unable to get successful response for request URL %s: %s", requestUrl, e.getMessage()));
            }

            return null;
        }
    };

    private final LoadingCache<String, String> requestCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(cacheLoader);

    @Override
    public FullGameSchedule getGamesForSeasonAndWeek(int season, int week) {
        // If they send 2018, assume they meant 2018-2019 season. Anything past the current year, default to current.
        String requestYear =
            Calendar.getInstance().get(Calendar.YEAR) < season ?
                "current" :
                String.format("%d-%d-regular", season, season + 1);

        String url =
            String.format("%s/%s/pull/nfl/%s/full_game_schedule.%s", baseUrl, apiVersion, requestYear, dataFormat);

        FullGameScheduleResponse response = getResponse(url, FullGameScheduleResponse.class);
        if (response == null) {
            throw new RuntimeException(
                String.format("No response retrieved for full game schedule request for season %d and week %d", season, week));
        }

        return response.getFullGameSchedule();
    }

    @Override
    public GameScore getGameScore(Instant instant, Integer msfGameId) {
        int season = SeasonUtil.getSeasonForDate(instant);
        String requestYear = String.format("%d-%d-regular", season, season + 1);

        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyyMMdd");

        String url =
            String.format(
                "https://api.mysportsfeeds.com/v1.2/pull/nfl/%s/scoreboard.json?fordate=%s",
                requestYear,
                formatter.format(instant.atZone(ZoneId.of("America/New_York"))));

        ScoreboardResponse response = getResponse(url, ScoreboardResponse.class);
        if (response == null || response.getScoreboard() == null) {
            throw new RuntimeException(String.format("No response retrieved for scoreboard request for date %s and game ID %d", instant.toString(), msfGameId));
        }

        if (CollectionUtils.isEmpty(response.getScoreboard().getGameScores())) {
            throw new RuntimeException(String.format("No games found for scoreboard request for date %s and game ID %d", instant.toString(), msfGameId));
        }

        Optional<GameScore> gameScore =
            response.getScoreboard().getGameScores()
                .stream()
                .filter(score -> score.getGame().getId().equals(msfGameId))
                .findFirst();

        if (!gameScore.isPresent()) {
            throw new RuntimeException(String.format("No game matching game ID %d found in scoreboard request for date %s", msfGameId, instant.toString()));
        }

        return gameScore.get();
    }

    private <T> T getResponse(String url, Class<T> clazz) {
        try {
            return MAPPER.readValue(requestCache.get(url), clazz);
        } catch (ExecutionException | IOException e) {
            log.trace("", e);
            log.warn(String.format("Exception occurred while attempting to get scoreboard response: %s", e.getMessage()));
        }

        return null;
    }
}
