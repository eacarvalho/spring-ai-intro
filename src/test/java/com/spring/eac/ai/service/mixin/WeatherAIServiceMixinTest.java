package com.spring.eac.ai.service.mixin;

import com.spring.eac.ai.model.Answer;
import com.spring.eac.ai.model.Question;
import com.spring.eac.ai.service.WeatherAIService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface WeatherAIServiceMixinTest {

    WeatherAIService getWeatherAIService();
    ChatModel getChatModel();

    @Test
    @DisplayName("Given a valid location, when asking for weather, then it should return the weather information.")
    default void getWeather_givenValidLocation_shouldReturnWeatherInformation() {
        // Given
        var question = new Question("What is the weather now considering the latitude 51.509865 and -0.118092 longitude?");

        // When
        Answer answer = getWeatherAIService().getWeather(question);

        // Then
        assertThat(answer).isNotNull();
        String actualAnswer = answer.answer().toLowerCase();
        assertTrue(actualAnswer.contains("temperature") || actualAnswer.contains("humidity") || actualAnswer.contains("wind"),
                "Answer should contain weather-related terms.");
    }

    @Test
    @DisplayName("Given a fictional location, when asking for weather, then it should handle it gracefully.")
    default void getWeather_givenFictionalLocation_shouldHandleGracefully() {
        // Given
        var question = new Question("What is the weather like in Narnia?");

        // When
        Answer answer = getWeatherAIService().getWeather(question);

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
    default void getWeather_givenNonWeatherQuestion_shouldRespondAccordingly() {
        // Given
        var question = new Question("What is the stock price of Microsoft?");

        // When
        Answer answer = getWeatherAIService().getWeather(question);

        // Then
        assertThat(answer).isNotNull();
        // The AI is instructed to answer questions about weather, so it should not answer about stocks.
        String actualAnswer = answer.answer().toLowerCase();
        assertFalse(actualAnswer.contains("temperature"), "Response should indicate its purpose is related to weather.");
    }

    /**
     * https://docs.spring.io/spring-ai/reference/api/testing.html
     */
    @Test
    @DisplayName("Given a fictional weather then evaluate the model against fact.")
    @Disabled("Disabling this test class temporarily")
    default void getWeather_givenFictionalWeather_shouldTestFactChecking() {
        // Create the FactCheckingEvaluator
        var factCheckingEvaluator = new FactCheckingEvaluator(ChatClient.builder(getChatModel()));

        // Example context and claim
        String context = "The Earth is the third planet from the Sun and the only astronomical object known to harbor life.";
        String claim = "The Earth is the fourth planet from the Sun.";

        // Create an EvaluationRequest
        EvaluationRequest evaluationRequest = new EvaluationRequest(context, Collections.emptyList(), claim);

        // Perform the evaluation
        EvaluationResponse evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);

        assertFalse(evaluationResponse.isPass(), "The claim should not be supported by the context");
    }
}
