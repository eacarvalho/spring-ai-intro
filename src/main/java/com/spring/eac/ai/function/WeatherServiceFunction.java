package com.spring.eac.ai.function;

import com.spring.eac.ai.model.WeatherRequest;
import com.spring.eac.ai.model.WeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.function.Function;

@Slf4j
public class WeatherServiceFunction implements Function<WeatherRequest, WeatherResponse> {

    public static final String WEATHER_URL = "https://api.api-ninjas.com/v1/weather";

    private final String ninjasApiKey;
    private final RestClient restClient;

    public WeatherServiceFunction(String ninjasApiKey) {
        this.ninjasApiKey = ninjasApiKey;
        this.restClient = createRestClient();
    }

    public WeatherServiceFunction(String ninjasApiKey, RestClient restClient) {
        this.ninjasApiKey = ninjasApiKey;
        this.restClient = restClient;
    }

    @Override
    public WeatherResponse apply(WeatherRequest weatherRequest) {
        // Validate input parameters
        if (weatherRequest == null) {
            log.error("WeatherRequest is null");
            throw new IllegalArgumentException("Weather request cannot be null");
        }

        // Validate coordinate ranges
        double lat = weatherRequest.lat();
        double lon = weatherRequest.lon();

        if (lat < -90 || lat > 90) {
            log.error("Invalid latitude: {}", lat);
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }

        if (lon < -180 || lon > 180) {
            log.error("Invalid longitude: {}", lon);
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }

        try {
            return restClient.get().uri(uriBuilder -> {
                log.info("Building URI for weather request: lat={}, lon={}", weatherRequest.lat(), weatherRequest.lon());

                uriBuilder.queryParam("lat", weatherRequest.lat());
                uriBuilder.queryParam("lon", weatherRequest.lon());

                String finalUri = uriBuilder.build().toString();
                log.info("Final URI: {}", finalUri);

                return uriBuilder.build();
            }).retrieve().body(WeatherResponse.class);

        } catch (HttpClientErrorException.BadRequest e) {
            log.error("Bad request to weather API: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Invalid weather request parameters: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Unauthorized access to weather API - check API key");
            throw new RuntimeException("Weather API authentication failed - check API key", e);
        } catch (HttpClientErrorException e) {
            log.error("HTTP error from weather API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Weather API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            log.error("Network error calling weather API: {}", e.getMessage());
            throw new RuntimeException("Failed to connect to weather API", e);
        } catch (Exception e) {
            log.error("Unexpected error calling weather API: {}", e.getMessage());
            throw new RuntimeException("Unexpected error retrieving weather data", e);
        }
    }

    private RestClient createRestClient() {
        return RestClient.builder()
                .baseUrl(WEATHER_URL)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("X-Api-Key", ninjasApiKey);
                    httpHeaders.set("Accept", "application/json");
                    httpHeaders.set("Content-Type", "application/json");
                }).build();
    }
}