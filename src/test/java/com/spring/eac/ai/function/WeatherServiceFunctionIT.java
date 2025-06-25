package com.spring.eac.ai.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.eac.ai.model.WeatherRequest;
import com.spring.eac.ai.model.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.wiremock.spring.EnableWireMock;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableWireMock
class WeatherServiceFunctionIT {

    WeatherServiceFunction weatherServiceFunction;

    public static String WEATHER_URL = "/v1/weather";
    private final String NINJAS_API_KEY = "test-api-key-123";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${wiremock.server.baseUrl}")
    private String wireMockBaseUrl;

    @BeforeEach
    void setup() {
        // Create RestClient with WireMock base URL
        RestClient testClient = RestClient.builder()
                .baseUrl(wireMockBaseUrl + WEATHER_URL)
                .defaultHeaders(headers -> {
                    headers.set("X-Api-Key", NINJAS_API_KEY);
                    headers.set("Accept", "application/json");
                }).build();

        weatherServiceFunction = new WeatherServiceFunction(NINJAS_API_KEY, testClient);
    }

    @Test
    void apply_WhenAmsterdamCoordinates_ShouldReturnWeatherData() throws Exception {
        // Given
        WeatherRequest request = new WeatherRequest(52.3676, 4.9041);
        WeatherResponse expectedResponse = createAmsterdamWeatherResponse();

        String responseJson = objectMapper.writeValueAsString(expectedResponse);

        stubFor(get(urlPathEqualTo(WEATHER_URL))
                .withQueryParam("lat", equalTo("52.3676"))
                .withQueryParam("lon", equalTo("4.9041"))
                .withHeader("X-Api-Key", equalTo(NINJAS_API_KEY))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseJson)));

        // When
        WeatherResponse result = weatherServiceFunction.apply(request);

        // Then
        assertNotNull(result);
        assertEquals("Amsterdam", result.location());
        assertEquals(18, result.temp());
        assertEquals(71, result.humidity());
        assertEquals(new BigDecimal("15.2"), result.windSpeed());
        assertEquals(225, result.windDegrees());
        assertEquals(1703530800, result.sunset());
        assertEquals(1703494800, result.sunrise());
        assertEquals(15, result.minTemp());
        assertEquals(58, result.cloudPct());
        assertEquals(16, result.feelsLike());
        assertEquals(21, result.maxTemp());

        // Verify the request was made correctly
        verify(getRequestedFor(urlPathEqualTo("/v1/weather")) // Remove the query parameters from urlPathEqualTo
                .withQueryParam("lat", equalTo("52.3676"))
                .withQueryParam("lon", equalTo("4.9041"))
                .withHeader("X-Api-Key", equalTo("test-api-key-123")));
    }

    @Test
    void apply_WhenTokyoCoordinates_ShouldReturnWeatherData() throws Exception {
        // Given
        WeatherRequest request = new WeatherRequest(35.6762, 139.6503);
        WeatherResponse expectedResponse = createTokyoWeatherResponse();

        String responseJson = objectMapper.writeValueAsString(expectedResponse);

        stubFor(get(urlPathEqualTo(WEATHER_URL))
                .withQueryParam("lat", equalTo("35.6762"))
                .withQueryParam("lon", equalTo("139.6503"))
                .withHeader("X-Api-Key", equalTo("test-api-key-123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseJson)
                        .withFixedDelay(100))); // Simulate network delay

        // When
        WeatherResponse result = weatherServiceFunction.apply(request);

        // Then
        assertNotNull(result);
        assertEquals("Tokyo", result.location());
        assertEquals(22, result.temp());
        assertEquals(75, result.humidity());
        assertEquals(new BigDecimal("8.3"), result.windSpeed());
        assertEquals(45, result.windDegrees());
        assertEquals(1703528400, result.sunset());
        assertEquals(1703492400, result.sunrise());
        assertEquals(19, result.minTemp());
        assertEquals(30, result.cloudPct());
        assertEquals(24, result.feelsLike());
        assertEquals(25, result.maxTemp());

        // Verify the request
        verify(getRequestedFor(urlPathEqualTo("/v1/weather"))
                .withQueryParam("lat", equalTo("35.6762"))
                .withQueryParam("lon", equalTo("139.6503")));
    }

    @Test
    void apply_WhenUnauthorizedApiKey_ShouldThrowRuntimeException() {
        // Given
        WeatherRequest request = new WeatherRequest(52.3676, 4.9041);

        stubFor(get(urlPathEqualTo(WEATHER_URL))
                .withQueryParam("lat", equalTo("52.3676"))
                .withQueryParam("lon", equalTo("4.9041"))
                .withHeader("X-Api-Key", equalTo("test-api-key-123"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Invalid API key\"}")));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> weatherServiceFunction.apply(request)
        );

        assertTrue(exception.getMessage().contains("Weather API authentication failed"));
        assertInstanceOf(HttpClientErrorException.Unauthorized.class, exception.getCause());

        // Verify the request was attempted
        verify(getRequestedFor(urlPathEqualTo("/v1/weather"))
                .withQueryParam("lat", equalTo("52.3676"))
                .withQueryParam("lon", equalTo("4.9041")));
    }

    @Test
    void apply_WhenPolarCoordinates_ShouldHandleExtremeLatitudes() throws Exception {
        // Given - Test with extreme coordinates
        WeatherRequest northPoleRequest = new WeatherRequest(90.0, 0.0);
        WeatherRequest southPoleRequest = new WeatherRequest(-90.0, 0.0);

        // Stub for North Pole
        stubFor(get(urlPathEqualTo(WEATHER_URL))
                .withQueryParam("lat", equalTo("90.0"))
                .withQueryParam("lon", equalTo("0.0"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(createNorthPoleWeatherResponse()))));

        // Stub for South Pole
        stubFor(get(urlPathEqualTo(WEATHER_URL))
                .withQueryParam("lat", equalTo("-90.0"))
                .withQueryParam("lon", equalTo("0.0"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(createSouthPoleWeatherResponse()))));

        // When
        WeatherResponse northResult = weatherServiceFunction.apply(northPoleRequest);
        WeatherResponse southResult = weatherServiceFunction.apply(southPoleRequest);

        // Then
        assertEquals("North Pole", northResult.location());
        assertEquals("South Pole Station", southResult.location());
        assertTrue(northResult.temp() < 0); // Cold at both poles
        assertTrue(southResult.temp() < 0);

        // Verify both requests were made
        verify(2, getRequestedFor(urlPathEqualTo("/v1/weather")));
    }

    private WeatherResponse createAmsterdamWeatherResponse() {
        return new WeatherResponse(
                "Amsterdam",                   // location
                new BigDecimal("15.2"),        // windSpeed
                225,                           // windDegrees
                18,                            // temp
                71,                            // humidity
                1703530800,                    // sunset
                1703494800,                    // sunrise
                15,                            // minTemp
                58,                            // cloudPct
                16,                            // feelsLike
                21                             // maxTemp
        );
    }

    private WeatherResponse createTokyoWeatherResponse() {
        return new WeatherResponse(
                "Tokyo",                       // location
                new BigDecimal("8.3"),         // windSpeed
                45,                            // windDegrees
                22,                            // temp
                75,                            // humidity
                1703528400,                    // sunset
                1703492400,                    // sunrise
                19,                            // minTemp
                30,                            // cloudPct
                24,                            // feelsLike
                25                             // maxTemp
        );
    }

    private WeatherResponse createNorthPoleWeatherResponse() {
        return new WeatherResponse(
                "North Pole",                  // location
                new BigDecimal("25.0"),        // windSpeed
                360,                           // windDegrees
                -35,                           // temp
                85,                            // humidity
                0,                             // sunset (polar night)
                0,                             // sunrise (polar night)
                -40,                           // minTemp
                90,                            // cloudPct
                -45,                           // feelsLike
                -30                            // maxTemp
        );
    }

    private WeatherResponse createSouthPoleWeatherResponse() {
        return new WeatherResponse(
                "South Pole Station",          // location
                new BigDecimal("8.3"),         // windSpeed
                180,                           // windDegrees
                -28,                           // temp
                65,                            // humidity
                0,                             // sunset
                0,                             // sunrise
                -32,                           // minTemp
                25,                            // cloudPct
                -35,                           // feelsLike
                -25                            // maxTemp
        );
    }
}
