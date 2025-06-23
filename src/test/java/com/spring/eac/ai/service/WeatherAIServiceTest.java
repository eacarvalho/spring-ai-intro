package com.spring.eac.ai.service;

import com.spring.eac.ai.config.InternalOllamaContainer;
import com.spring.eac.ai.model.Answer;
import com.spring.eac.ai.model.Question;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.ollama.OllamaContainer;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Disabling this test class temporarily")
@Testcontainers
@SpringBootTest
@TestPropertySource(properties = {
        "spring.ai.ollama.chat.options.model=" + InternalOllamaContainer.MODEL_NAME,
        "spring.ai.chat.client.enabled=false"
})
@Slf4j
class WeatherAIServiceTest {

    @Container
    @ServiceConnection
    static OllamaContainer ollama = new InternalOllamaContainer("internal/" + InternalOllamaContainer.MODEL_NAME);

    @Autowired
    private WeatherAIService weatherAIService;

    @BeforeAll
    public static void beforeAll() throws IOException, InterruptedException {
        if (!ollama.isRunning()) {
            ollama.start();
        }
    }

    @Test
    @DisplayName("Given a valid location, when asking for weather, then it should return the weather information.")
    void getWeather_givenValidLocation_shouldReturnWeatherInformation() {
        // Given
        var question = new Question("What is the weather now considering the latitude 51.509865 and -0.118092 longitude?");

        // When
        Answer answer = weatherAIService.getWeather(question);

        // Then
        assertThat(answer).isNotNull();
        String actualAnswer = answer.answer().toLowerCase();
        assertTrue(actualAnswer.contains("temperature") || actualAnswer.contains("humidity") || actualAnswer.contains("wind"),
                "Answer should contain weather-related terms.");
    }

    @Test
    @DisplayName("Given a fictional location, when asking for weather, then it should handle it gracefully.")
    void getWeather_givenFictionalLocation_shouldHandleGracefully() {
        // Given
        var question = new Question("What is the weather like in Narnia?");

        // When
        Answer answer = weatherAIService.getWeather(question);

        // Then
        assertThat(answer).isNotNull();
        // The AI should indicate that it cannot find the location or provide weather for it.
        // The exact wording can vary, so we check for common phrases.
        String actualAnswer = answer.answer().toLowerCase();
        assertTrue(actualAnswer.contains("narnia") || actualAnswer.contains("could not find") || actualAnswer.contains("unable to find"),
                "Should gracefully handle a fictional location.");
    }

    @Test
    @DisplayName("Given a non-weather related question, when asking, then it should respond accordingly.")
    void getWeather_givenNonWeatherQuestion_shouldRespondAccordingly() {
        // Given
        var question = new Question("What is the stock price of Microsoft?");

        // When
        Answer answer = weatherAIService.getWeather(question);

        // Then
        assertThat(answer).isNotNull();
        // The AI is instructed to answer questions about weather, so it should not answer about stocks.
        String actualAnswer = answer.answer().toLowerCase();
        assertFalse(actualAnswer.contains("temperature"), "Response should indicate its purpose is related to weather.");
    }
}