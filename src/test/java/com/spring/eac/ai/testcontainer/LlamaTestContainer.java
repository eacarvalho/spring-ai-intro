package com.spring.eac.ai.testcontainer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.ollama.OllamaContainer;

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestPropertySource(properties = {
        "spring.ai.model.chat=ollama",
        "spring.ai.model.embedding=ollama"
})
public abstract class LlamaTestContainer {
    private static final String MODEL_NAME = "llama3.1:8b";

    @Container
    @ServiceConnection
    static OllamaContainer ollama = OllamaContainerFactory.createContainer(MODEL_NAME);

    @DynamicPropertySource
    static void ollamaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.ai.ollama.chat.options.model", () -> MODEL_NAME);
    }
}
