package com.spring.eac.ai.function;

import com.fasterxml.jackson.databind.JsonNode;
import com.spring.eac.ai.model.StockPriceRequest;
import com.spring.eac.ai.model.StockPriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.function.Function;

@Slf4j
public class StockPriceServiceFunction implements Function<StockPriceRequest, StockPriceResponse> {

    public static final String STOCK_URL = "https://api.api-ninjas.com/v1/stockprice";

    private final RestClient restClient;

    public StockPriceServiceFunction(String ninjasApiKey) {
        this.restClient = RestClient.builder()
                .baseUrl(STOCK_URL)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("X-Api-Key", ninjasApiKey);
                    httpHeaders.set("Accept", "application/json");
                    httpHeaders.set("Content-Type", "application/json");
                })
                .requestInterceptor((request, body, execution) -> {
                    log.info("Final URI: {}", request.getURI());
                    return execution.execute(request, body);
                })
                .build();
    }

    public StockPriceServiceFunction(String ninjasApiKey, RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public StockPriceResponse apply(StockPriceRequest stockPriceRequest) {
        // Validate input parameters
        if (stockPriceRequest == null || stockPriceRequest.ticker() == null) {
            log.error("StockPriceRequest is null");
            throw new IllegalArgumentException("Ticker request cannot be null");
        }

        try {
            JsonNode jsonNode = restClient.get()
                    .uri("?ticker={ticker}", stockPriceRequest.ticker())
                    .retrieve()
                    .body(JsonNode.class);

            if (ObjectUtils.isEmpty(jsonNode) || jsonNode.isEmpty()) {
                return null;
            }

            return ModelOptionsUtils.jsonToObject(jsonNode.toString(), StockPriceResponse.class);
        } catch (HttpClientErrorException.BadRequest e) {
            log.error("Bad request to stock price API: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Invalid stock price request parameters: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Unauthorized access to stock price API - check API key");
            throw new RuntimeException("Stock price API authentication failed - check API key", e);
        } catch (HttpClientErrorException e) {
            log.error("HTTP error from Stock price API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Stock price API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            log.error("Network error calling stock price API", e);
            throw new RuntimeException("Failed to connect to stock price API", e);
        } catch (Exception e) {
            log.error("Unexpected error calling stock price API", e);
            throw new RuntimeException("Unexpected error retrieving stock price data", e);
        }
    }
}