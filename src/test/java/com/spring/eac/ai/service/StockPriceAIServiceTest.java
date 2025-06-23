package com.spring.eac.ai.service;

import com.spring.eac.ai.model.Answer;
import com.spring.eac.ai.model.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class StockPriceAIServiceTest {

    @Autowired
    private StockPriceAIService stockPriceAIService;

    @Test
    @DisplayName("Given a valid company name, when asking for stock price, then it should return the stock price.")
    void getStockPrice_givenValidCompanyName_shouldReturnStockPrice() {
        // Given
        var question = new Question("What is the stock price of Apple?");

        // When
        Answer actualAnswer = stockPriceAIService.getStockPrice(question);

        // Then
        String answer = actualAnswer.answer().toLowerCase();
        assertTrue(answer.contains("apple"), "Answer should contain the company name.");
        assertTrue(answer.contains("price"), "Answer should contain the word 'price'.");
        assertTrue(answer.contains("aapl"), "Answer should contain the ticker 'AAPL'.");
    }

    @Test
    @DisplayName("Given a company not on a US exchange, when asking for stock price, then it should return a not supported message.")
    void getStockPrice_givenNonUSCompany_shouldReturnNotSupportedMessage() {
        // Given
        var question = new Question("What is the stock price of Renault?"); // A known non-US company

        // When
        Answer actualAnswer = stockPriceAIService.getStockPrice(question);

        // Then
        assertTrue(actualAnswer.answer().contains("Only America Stock Exchange is supported for now"),
                "Should return the unsupported exchange message.");
    }

    @Test
    @DisplayName("Given a non-stock related question, when asking, then it should return a graceful response.")
    void getStockPrice_givenNonStockRelatedQuestion_shouldReturnGracefulResponse() {
        // Given
        var question = new Question("What is the weather like today?");

        // When
        Answer actualAnswer = stockPriceAIService.getStockPrice(question);

        // Then
        assertTrue(actualAnswer.answer().contains("I can only answer questions about stock prices."),
                "Should return a message indicating it only handles stock price questions.");
    }
}