package com.spring.eac.ai.testcontainer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.ollama.OllamaContainer;

@Testcontainers(disabledWithoutDocker = true)
@TestPropertySource(properties = {
        "spring.ai.chat.client.enabled=false"
})
@Slf4j
public abstract class GemmaTestContainer {
    private static final String MODEL_NAME = "gemma2:2b";

    @Container
    @ServiceConnection
    static OllamaContainer ollama = OllamaContainerFactory.createContainer(MODEL_NAME);

    @DynamicPropertySource
    static void ollamaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.ai.ollama.chat.options.model", () -> MODEL_NAME);
    }
}
