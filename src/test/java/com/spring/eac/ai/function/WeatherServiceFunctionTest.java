
package com.spring.eac.ai.function;

import com.spring.eac.ai.model.WeatherRequest;
import com.spring.eac.ai.model.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceFunctionTest {

    private WeatherServiceFunction weatherServiceFunction;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec<?> uriSpec;

    @Mock
    private RestClient.RequestHeadersSpec<?> headerSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        // Create the service with the builder mock
        weatherServiceFunction = new WeatherServiceFunction("test-api-key-123", restClient);
    }

    @Test
    void apply_WhenWeatherRequestIsNull_ShouldThrowIllegalArgumentException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> weatherServiceFunction.apply(null)
        );

        assertEquals("Weather request cannot be null", exception.getMessage());
        verifyNoInteractions(restClient);
    }

    @Test
    void apply_WhenLatitudeIsTooLow_ShouldThrowIllegalArgumentException() {
        // Given
        WeatherRequest request = new WeatherRequest(-91.0, 0.0);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> weatherServiceFunction.apply(request)
        );

        assertEquals("Latitude must be between -90 and 90 degrees", exception.getMessage());
        verifyNoInteractions(restClient);
    }

    @Test
    void apply_WhenLatitudeIsTooHigh_ShouldThrowIllegalArgumentException() {
        // Given
        WeatherRequest request = new WeatherRequest(91.0, 0.0);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> weatherServiceFunction.apply(request)
        );

        assertEquals("Latitude must be between -90 and 90 degrees", exception.getMessage());
        verifyNoInteractions(restClient);
    }

    @Test
    void apply_WhenLongitudeIsTooLow_ShouldThrowIllegalArgumentException() {
        // Given
        WeatherRequest request = new WeatherRequest(0.0, -181.0);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> weatherServiceFunction.apply(request)
        );

        assertEquals("Longitude must be between -180 and 180 degrees", exception.getMessage());
        verifyNoInteractions(restClient);
    }

    @Test
    void apply_WhenLongitudeIsTooHigh_ShouldThrowIllegalArgumentException() {
        // Given
        WeatherRequest request = new WeatherRequest(0.0, 181.0);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> weatherServiceFunction.apply(request)
        );

        assertEquals("Longitude must be between -180 and 180 degrees", exception.getMessage());
        verifyNoInteractions(restClient);
    }

    @Test
    void apply_WhenValidRequest_ShouldReturnWeatherResponse() {
        // Given
        WeatherRequest request = new WeatherRequest(52.3676, 4.9041);
        WeatherResponse expectedResponse = new WeatherResponse(
                "Amsterdam",                  // location
                new BigDecimal("15.5"),       // windSpeed
                65,                           // windDegrees
                20,                           // temp
                75,                           // humidity
                1656789000,                   // sunset
                1656743400,                   // sunrise
                15,                           // minTemp
                30,                           // cloudPct
                22,                           // feelsLike
                25                            // maxTemp
        );

        // Mock the RestClient chain
        doReturn(uriSpec).when(restClient).get();
        doReturn(headerSpec).when(uriSpec).uri(any(Function.class));
        doReturn(responseSpec).when(headerSpec).retrieve();
        doReturn(expectedResponse).when(responseSpec).body(WeatherResponse.class);

        // When
        WeatherResponse result = weatherServiceFunction.apply(request);

        // Then
        assertNotNull(result);
        assertEquals(expectedResponse, result);

        verify(restClient).get();
        verify(uriSpec).uri(any(Function.class));
        verify(headerSpec).retrieve();
        verify(responseSpec).body(WeatherResponse.class);
    }

    @Test
    void apply_WhenBoundaryCoordinates_ShouldCallRestClient() {
        // Given
        WeatherRequest request = new WeatherRequest(-90.0, 180.0);
        WeatherResponse expectedResponse = new WeatherResponse(
                "South Pole",                 // location
                new BigDecimal("30.0"),       // windSpeed
                90,                           // windDegrees
                -30,                          // temp
                60,                           // humidity
                1656789000,                   // sunset
                1656743400,                   // sunrise
                -35,                          // minTemp
                10,                           // cloudPct
                -40,                          // feelsLike
                -25                           // maxTemp
        );

        // Mock the RestClient chain
        doReturn(uriSpec).when(restClient).get();
        doReturn(headerSpec).when(uriSpec).uri(any(Function.class));
        doReturn(responseSpec).when(headerSpec).retrieve();
        doReturn(expectedResponse).when(responseSpec).body(WeatherResponse.class);

        // When
        WeatherResponse result = weatherServiceFunction.apply(request);

        // Then
        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    void apply_WhenBadRequest_ShouldThrowRuntimeException() {
        // Given
        WeatherRequest request = new WeatherRequest(52.3676, 4.9041);
        HttpClientErrorException badRequestException = new HttpClientErrorException(
                HttpStatus.BAD_REQUEST, "Bad Request", "Invalid coordinates".getBytes(), null
        );

        // Mock the RestClient chain to throw exception
        doReturn(uriSpec).when(restClient).get();
        doReturn(headerSpec).when(uriSpec).uri(any(Function.class));
        doReturn(responseSpec).when(headerSpec).retrieve();
        doThrow(badRequestException).when(responseSpec).body(WeatherResponse.class);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> weatherServiceFunction.apply(request)
        );

        assertFalse(exception.getMessage().contains("Invalid weather request parameters"));
        assertEquals(badRequestException, exception.getCause());
    }

    @Test
    void apply_WhenUnauthorized_ShouldThrowRuntimeException() {
        // Given
        WeatherRequest request = new WeatherRequest(52.3676, 4.9041);
        HttpClientErrorException unauthorizedException = new HttpClientErrorException(
                HttpStatus.UNAUTHORIZED, "Unauthorized"
        );

        // Mock the RestClient chain to throw exception
        doReturn(uriSpec).when(restClient).get();
        doReturn(headerSpec).when(uriSpec).uri(any(Function.class));
        doReturn(responseSpec).when(headerSpec).retrieve();
        doThrow(unauthorizedException).when(responseSpec).body(WeatherResponse.class);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> weatherServiceFunction.apply(request)
        );

        assertFalse(exception.getMessage().contains("Weather API authentication failed"));
        assertEquals(unauthorizedException, exception.getCause());
    }

    @Test
    void apply_WhenHttpClientError_ShouldThrowRuntimeException() {
        // Given
        WeatherRequest request = new WeatherRequest(52.3676, 4.9041);
        HttpClientErrorException httpException = new HttpClientErrorException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", "Internal error".getBytes(), null
        );

        // Mock the RestClient chain to throw exception
        doReturn(uriSpec).when(restClient).get();
        doReturn(headerSpec).when(uriSpec).uri(any(Function.class));
        doReturn(responseSpec).when(headerSpec).retrieve();
        doThrow(httpException).when(responseSpec).body(WeatherResponse.class);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> weatherServiceFunction.apply(request)
        );

        assertTrue(exception.getMessage().contains("Weather API error"));
        assertTrue(exception.getMessage().contains("500"));
        assertEquals(httpException, exception.getCause());
    }

    @Test
    void apply_WhenRestClientException_ShouldThrowRuntimeException() {
        // Given
        WeatherRequest request = new WeatherRequest(52.3676, 4.9041);
        RestClientException networkException = new RestClientException("Network timeout");

        // Mock the RestClient chain to throw exception
        doReturn(uriSpec).when(restClient).get();
        doReturn(headerSpec).when(uriSpec).uri(any(Function.class));
        doReturn(responseSpec).when(headerSpec).retrieve();
        doThrow(networkException).when(responseSpec).body(WeatherResponse.class);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> weatherServiceFunction.apply(request)
        );

        assertTrue(exception.getMessage().contains("Failed to connect to weather API"));
        assertEquals(networkException, exception.getCause());
    }

    @Test
    void apply_WhenUnexpectedException_ShouldThrowRuntimeException() {
        // Given
        WeatherRequest request = new WeatherRequest(52.3676, 4.9041);
        RuntimeException unexpectedException = new RuntimeException("Unexpected error");

        // Mock the RestClient chain to throw exception
        doReturn(uriSpec).when(restClient).get();
        doReturn(headerSpec).when(uriSpec).uri(any(Function.class));
        doReturn(responseSpec).when(headerSpec).retrieve();
        doThrow(unexpectedException).when(responseSpec).body(WeatherResponse.class);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> weatherServiceFunction.apply(request)
        );

        assertTrue(exception.getMessage().contains("Unexpected error retrieving weather data"));
        assertEquals(unexpectedException, exception.getCause());
    }

    @Test
    void apply_WhenZeroCoordinates_ShouldCallRestClient() {
        // Given
        WeatherRequest request = new WeatherRequest(0.0, 0.0);
        WeatherResponse expectedResponse = new WeatherResponse(
                "Null Island",                // location
                new BigDecimal("5.0"),        // windSpeed
                180,                          // windDegrees
                28,                           // temp
                80,                           // humidity
                1656789000,                   // sunset
                1656743400,                   // sunrise
                25,                           // minTemp
                20,                           // cloudPct
                30,                           // feelsLike
                32                            // maxTemp
        );

        // Mock the RestClient chain
        doReturn(uriSpec).when(restClient).get();
        doReturn(headerSpec).when(uriSpec).uri(any(Function.class));
        doReturn(responseSpec).when(headerSpec).retrieve();
        doReturn(expectedResponse).when(responseSpec).body(WeatherResponse.class);

        // When
        WeatherResponse result = weatherServiceFunction.apply(request);

        // Then
        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }
}