package com.spring.eac.ai.service;

import com.spring.eac.ai.model.Answer;
import com.spring.eac.ai.model.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
class StockPriceAIServiceTest {

    @Autowired
    private StockPriceAIService stockPriceAIService;

    @MockitoBean
    private ChatModel chatModel;

    @Test
    @DisplayName("Given a valid company name, when asking for stock price, then it should return the stock price.")
    void getStockPrice_givenValidCompanyName_shouldReturnStockPrice() {
        // Given
        var question = new Question("What is the stock price of Apple?");
        var expectedResponse = "The stock price of Apple Inc. (AAPL) is approximately XYZ USD.";
        var chatResponse = new ChatResponse(Collections.singletonList(new Generation(new AssistantMessage(expectedResponse))));
        doReturn(chatResponse).when(chatModel).call(any(Prompt.class));

        // When
        Answer actualAnswer = stockPriceAIService.getStockPrice(question);

        // Then
        assertEquals(expectedResponse, actualAnswer.answer());
    }

    @Test
    @DisplayName("Given a company not on a US exchange, when asking for stock price, then it should return a not supported message.")
    void getStockPrice_givenNonUSCompany_shouldReturnNotSupportedMessage() {
        // Given
        var question = new Question("What is the stock price of a non-US company?");
        var expectedResponse = "Only America Stock Exchange is supported for now.";
        var chatResponse = new ChatResponse(Collections.singletonList(new Generation(new AssistantMessage(expectedResponse))));
        doReturn(chatResponse).when(chatModel).call(any(Prompt.class));

        // When
        Answer actualAnswer = stockPriceAIService.getStockPrice(question);

        // Then
        assertEquals(expectedResponse, actualAnswer.answer());
    }

    @Test
    @DisplayName("Given a non-stock related question, when asking, then it should return a graceful response.")
    void getStockPrice_givenNonStockRelatedQuestion_shouldReturnGracefulResponse() {
        // Given
        var question = new Question("What is the weather like today?");
        var expectedResponse = "I can only answer questions about stock prices.";
        var chatResponse = new ChatResponse(Collections.singletonList(new Generation(new AssistantMessage(expectedResponse))));
        doReturn(chatResponse).when(chatModel).call(any(Prompt.class));

        // When
        Answer actualAnswer = stockPriceAIService.getStockPrice(question);

        // Then
        assertEquals(expectedResponse, actualAnswer.answer());
    }
}