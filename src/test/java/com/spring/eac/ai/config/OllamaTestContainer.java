package com.spring.eac.ai.config;

import com.github.dockerjava.api.model.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.List;

@Testcontainers(disabledWithoutDocker = true)
@TestPropertySource(properties = {
        "spring.ai.chat.client.enabled=false"
})
@Slf4j
public abstract class OllamaTestContainer {

    private static final String MODEL_NAME = "llama3.1:8b";
    private static final String TC_IMAGE_NAME = "internal/tc-ollama-" + MODEL_NAME;
    private static final String OLLAMA_DOCKER_IMAGE = "ollama/ollama:0.9.2";

    @Container
    @ServiceConnection
    static OllamaContainer ollama = new OllamaContainer(DockerImageName.parse(TC_IMAGE_NAME)
            .asCompatibleSubstituteFor(OLLAMA_DOCKER_IMAGE))
            .withReuse(true);

    static {
        List<Image> listImagesCmd = DockerClientFactory
                .lazyClient()
                .listImagesCmd()
                .withImageNameFilter(TC_IMAGE_NAME)
                .exec();

        if (listImagesCmd.isEmpty()) {
            createImage();
        } else {
            log.info("Using existing Ollama container with {} image...", MODEL_NAME);
            // Substitute the default Ollama image with our Gemma variant
            ollama.start();
        }
    }

    @DynamicPropertySource
    static void ollamaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.ai.ollama.chat.options.model", MODEL_NAME::toString);
    }

    private static void createImage() {
        try {
            log.info("Creating a new Ollama container with {} image...", MODEL_NAME);
            OllamaContainer ollama = new OllamaContainer(OLLAMA_DOCKER_IMAGE);
            ollama.start();

            log.info("Start pulling the '{}' generative ... would take several minutes ...", MODEL_NAME);
            ollama.execInContainer("ollama", "pull", MODEL_NAME);
            log.info("{} pulling competed!", MODEL_NAME);

            log.info("Start committing the '{}' image ... would take several minutes ...", TC_IMAGE_NAME);
            ollama.commitToImage(TC_IMAGE_NAME);
            log.info("Finish committing the '{}' image!", TC_IMAGE_NAME);
        } catch (IOException | InterruptedException e) {
            log.error("Failed to create Ollama container with {} image: {}", MODEL_NAME, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
