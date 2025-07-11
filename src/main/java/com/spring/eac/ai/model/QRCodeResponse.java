package com.spring.eac.ai.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonClassDescription("QRCode response")
public record QRCodeResponse(@JsonPropertyDescription("The QRCode response in byte array") byte[] imageData) {
}