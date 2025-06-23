package com.spring.eac.ai.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonClassDescription("Weather API request")
public record WeatherRequest(
        @JsonProperty(required = true, value = "lat")
        @JsonPropertyDescription("The Latitude of desired location e.g. 51.5074")
        double lat,
        @JsonProperty(required = true, value = "lon")
        @JsonPropertyDescription("The Longitude of desired location e.g. -0.1278")
        double lon){
}