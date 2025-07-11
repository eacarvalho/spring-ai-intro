package com.spring.eac.ai.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonClassDescription("QRCode request")
public record QRCodeRequest(
        @JsonPropertyDescription("The input data to convert to QRCode") String data,
        @JsonPropertyDescription("The image format to return, if format is not provided use PNG") String format) {
}