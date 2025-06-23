package com.spring.eac.ai.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonClassDescription("Stock price request")
public record StockPriceRequest(@JsonPropertyDescription("ticker name of the stock to quote") String ticker) {
}