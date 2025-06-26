package com.spring.eac.ai.service.mixin;

import com.spring.eac.ai.service.WeatherAIService;
import com.spring.eac.ai.testcontainer.LlamaTestContainer;
import com.spring.eac.ai.testcontainer.MistralTestContainer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("Weather AI Service Tests")
class WeatherAIServiceIT {

    @Nested
    @SpringBootTest
    @DisplayName("With Llama Model")
    class WeatherAIServiceLlamaIT extends LlamaTestContainer implements WeatherAIServiceMixinTest  {

        @Autowired
        private WeatherAIService weatherAIService;

        @Autowired
        private ChatModel chatModel;

        @Override
        public WeatherAIService getWeatherAIService() {
            return weatherAIService;
        }

        @Override
        public ChatModel getChatModel() {
            return chatModel;
        }
    }

    @Nested
    @SpringBootTest
    @DisplayName("With Mistral Model")
    @Disabled("Disabling this test class temporarily")
    class WeatherAIServiceMistralIT extends MistralTestContainer implements WeatherAIServiceMixinTest  {

        @Autowired
        private WeatherAIService weatherAIService;

        @Autowired
        private ChatModel chatModel;

        @Override
        public WeatherAIService getWeatherAIService() {
            return weatherAIService;
        }

        @Override
        public ChatModel getChatModel() {
            return chatModel;
        }
    }

    @Nested
    @SpringBootTest
    @DisplayName("With GPT Model Model")
    @Disabled("Disabling this test class temporarily")
    class WeatherAIServiceGptIT implements WeatherAIServiceMixinTest  {

        @Autowired
        private WeatherAIService weatherAIService;

        @Autowired
        private ChatModel chatModel;

        @Override
        public WeatherAIService getWeatherAIService() {
            return weatherAIService;
        }

        @Override
        public ChatModel getChatModel() {
            return chatModel;
        }
    }
}
