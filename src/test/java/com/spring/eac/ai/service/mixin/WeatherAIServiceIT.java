package com.spring.eac.ai.service.mixin;

import com.spring.eac.ai.service.WeatherAIService;
import com.spring.eac.ai.testcontainer.LlamaTestContainer;
import com.spring.eac.ai.testcontainer.MistralTestContainer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Disabling this test class temporarily")
@DisplayName("Weather AI Service Tests")
public class WeatherAIServiceIT {

    @Nested
    @SpringBootTest
    @DisplayName("With Llama Model")
    @Disabled("Disabling this test class temporarily")
    class WeatherAIServiceLlamaTest extends LlamaTestContainer implements WeatherAIServiceMixinTest  {

        @Autowired
        private WeatherAIService weatherAIService;

        @Override
        public WeatherAIService getWeatherAIService() {
            return weatherAIService;
        }
    }

    @Nested
    @SpringBootTest
    @DisplayName("With Mistral Model")
    @Disabled("Disabling this test class temporarily")
    class WeatherAIServiceMistralTest extends MistralTestContainer implements WeatherAIServiceMixinTest  {

        @Autowired
        private WeatherAIService weatherAIService;

        @Override
        public WeatherAIService getWeatherAIService() {
            return weatherAIService;
        }
    }

    @Nested
    @SpringBootTest
    @DisplayName("With GPT Model Model")
    @Disabled("Disabling this test class temporarily")
    class WeatherAIServiceGptTest implements WeatherAIServiceMixinTest  {

        @Autowired
        private WeatherAIService weatherAIService;

        @Override
        public WeatherAIService getWeatherAIService() {
            return weatherAIService;
        }
    }
}
