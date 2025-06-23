package com.spring.eac.ai.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.math.BigDecimal;

/*
{
  "ticker": "AAPL",
  "name": "Apple Inc.",
  "price": 192.42,
  "exchange": "NASDAQ",
  "updated": 1706302801
}
 */
@JsonClassDescription("Stock price response")
public record StockPriceResponse(@JsonPropertyDescription("ticker symbol of stock") String ticker,
                                 @JsonPropertyDescription("Company name") String name,
                                 @JsonPropertyDescription("Price of stock in USD") BigDecimal price,
                                 @JsonPropertyDescription("The exchange the stock is traded on") String exchange,
                                 @JsonPropertyDescription("Epoch Time of quote") Integer updated) {
}