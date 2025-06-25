package com.spring.eac.ai.function;

import com.spring.eac.ai.model.WeatherRequest;
import com.spring.eac.ai.model.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;

import java.math.BigDecimal;
import java.net.URI;
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
    @DisplayName("Should throw IllegalArgumentException when weather request is null")
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
    @DisplayName("Should throw IllegalArgumentException when latitude is below -90 degrees")
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
    @DisplayName("Should throw IllegalArgumentException when latitude is above 90 degrees")
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
    @DisplayName("Should throw IllegalArgumentException when longitude is below -180 degrees")
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
    @DisplayName("Should throw IllegalArgumentException when longitude is above 180 degrees")
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
    @DisplayName("Should return weather response when valid request is provided")
    void apply_WhenValidRequest_ShouldReturnWeatherResponse() {
        // Given
        WeatherRequest request = new WeatherRequest(52.3676, 4.9041);
        WeatherResponse expectedResponse = new WeatherResponse(
                "Amsterdam",          // location
                new BigDecimal("15.5"),   // windSpeed
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
    @DisplayName("Should call RestClient when boundary coordinates are provided")
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
    @DisplayName("Should throw RuntimeException when weather API returns bad request")
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
    @DisplayName("Should throw RuntimeException when weather API returns unauthorized")
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
    @DisplayName("Should throw RuntimeException when weather API returns HTTP client error")
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
    @DisplayName("Should throw RuntimeException when RestClient throws network exception")
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
    @DisplayName("Should throw RuntimeException when unexpected exception occurs")
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
    @DisplayName("Should call RestClient when zero coordinates are provided")
    void apply_WhenZeroCoordinates_ShouldCallRestClient() {
        // Given
        WeatherRequest request = new WeatherRequest(0.0, 0.0);
        WeatherResponse expectedResponse = new WeatherResponse(
                "Null Island",        // location
                new BigDecimal("5.0"),    // windSpeed
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

    @Test
    @DisplayName("Should build correct URI when valid request is provided")
    void apply_WhenValidRequest_ShouldBuildCorrectURI() {
        // Given
        WeatherRequest request = new WeatherRequest(52.3676, 4.9041);
        WeatherResponse expectedResponse =  new WeatherResponse(
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
        );;

        // Mock UriBuilder
        UriBuilder mockUriBuilder = mock(UriBuilder.class);
        URI mockUri = URI.create("https://api.api-ninjas.com/v1/weather?lat=52.3676&lon=4.9041");

        when(mockUriBuilder.queryParam("lat", 52.3676)).thenReturn(mockUriBuilder);
        when(mockUriBuilder.queryParam("lon", 4.9041)).thenReturn(mockUriBuilder);
        when(mockUriBuilder.build()).thenReturn(mockUri);

        // Capture the URI function
        ArgumentCaptor<Function<UriBuilder, URI>> uriFunctionCaptor = ArgumentCaptor.forClass(Function.class);

        // Mock the RestClient chain
        doReturn(uriSpec).when(restClient).get();
        doReturn(headerSpec).when(uriSpec).uri(uriFunctionCaptor.capture());
        doReturn(responseSpec).when(headerSpec).retrieve();
        when(responseSpec.body(WeatherResponse.class)).thenReturn(expectedResponse);

        // When
        WeatherResponse result = weatherServiceFunction.apply(request);

        // Then
        assertNotNull(result);

        // Verify the URI function was captured and execute it
        Function<UriBuilder, URI> capturedUriFunction = uriFunctionCaptor.getValue();
        assertNotNull(capturedUriFunction);

        // Execute the captured function with our mock UriBuilder
        URI resultUri = capturedUriFunction.apply(mockUriBuilder);

        // Verify the URI building logic
        verify(mockUriBuilder).queryParam("lat", 52.3676);
        verify(mockUriBuilder).queryParam("lon", 4.9041);
        verify(mockUriBuilder, times(2)).build(); // Called twice in the lambda
        assertEquals(mockUri, resultUri);
    }
}