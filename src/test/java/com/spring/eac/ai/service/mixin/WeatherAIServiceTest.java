package com.spring.eac.ai.service.mixin;

import com.spring.eac.ai.service.WeatherAIService;
import com.spring.eac.ai.testcontainer.MistralTestContainer;
import com.spring.eac.ai.testcontainer.LlamaTestContainer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Disabling this test class temporarily")
@SpringBootTest
@DisplayName("Weather AI Service Tests")
public class WeatherAIServiceTest {

    @SpringBootTest
    @DisplayName("With Llama Model")
    @Disabled("Disabling this test class temporarily")
    static class WeatherAIServiceLlamaTest extends LlamaTestContainer implements WeatherAIServiceMixinTest  {

        @Autowired
        private WeatherAIService weatherAIService;

        @Override
        public WeatherAIService getWeatherAIService() {
            return weatherAIService;
        }
    }

    @SpringBootTest
    @DisplayName("With Gemma Model")
    @Disabled("Disabling this test class temporarily")
    static class WeatherAIServiceMistralTest extends MistralTestContainer implements WeatherAIServiceMixinTest  {

        @Autowired
        private WeatherAIService weatherAIService;

        @Override
        public WeatherAIService getWeatherAIService() {
            return weatherAIService;
        }
    }
}
