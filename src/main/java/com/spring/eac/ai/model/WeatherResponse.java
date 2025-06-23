
package com.spring.eac.ai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.math.BigDecimal;

public record WeatherResponse(
        @JsonProperty("location")
        @JsonPropertyDescription("The location of the weather")
        String location,

        @JsonProperty("wind_speed")
        @JsonPropertyDescription("WindSpeed in KMH")
        BigDecimal windSpeed,

        @JsonProperty("wind_degrees")
        @JsonPropertyDescription("Direction of wind")
        Integer windDegrees,

        @JsonProperty("temp")
        @JsonPropertyDescription("Current Temperature in Celsius")
        Integer temp,

        @JsonProperty("humidity")
        @JsonPropertyDescription("Current Humidity")
        Integer humidity,

        @JsonProperty("sunset")
        @JsonPropertyDescription("Epoch time of sunset GMT")
        Integer sunset,

        @JsonProperty("sunrise")
        @JsonPropertyDescription("Epoch time of Sunrise GMT")
        Integer sunrise,

        @JsonProperty("min_temp")
        @JsonPropertyDescription("Low Temperature in Celsius")
        Integer minTemp,

        @JsonProperty("cloud_pct")
        @JsonPropertyDescription("Cloud Coverage Percentage")
        Integer cloudPct,

        @JsonProperty("feels_like")
        @JsonPropertyDescription("Temperature in Celsius")
        Integer feelsLike,

        @JsonProperty("max_temp")
        @JsonPropertyDescription("Maximum Temperature in Celsius")
        Integer maxTemp
) {

    @Override
    public String toString() {
        return String.format(
                "Weather{location=%s, temp=%d°C, feels_like=%d°C, humidity=%d%%, wind_speed=%.2f km/h, " +
                        "wind_degrees=%d°, cloud_pct=%d%%, min_temp=%d°C, max_temp=%d°C, " +
                        "sunrise=%d, sunset=%d}",
                location, temp, feelsLike, humidity, windSpeed, windDegrees, cloudPct,
                minTemp, maxTemp, sunrise, sunset
        );
    }
}