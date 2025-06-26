package com.spring.eac.ai.testcontainer;

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

/**
 * Abstract base class for integration tests requiring an Ollama instance.
 * <p>
 * This class leverages Testcontainers to manage the lifecycle of an Ollama Docker container.
 * It is designed to optimize test execution time by creating a custom Docker image
 * with a pre-pulled language model, and reusing it across test runs.
 *
 * <h2>Container Setup and Model Caching</h2>
 * On the first run, this class will:
 * <ol>
 *     <li>Start a standard Ollama container from the {@code ollama/ollama:0.9.3} image.</li>
 *     <li>Pull the specified language model (e.g., {@code llama3.1:8b}) into the container.</li>
 *     <li>Commit the container with the downloaded model to a new Docker image with a dedicated tag.</li>
 * </ol>
 * For subsequent test runs, the class will reuse the custom-built image, significantly
 * speeding up container startup time. The container is also configured with {@code .withReuse(true)}
 * to persist between test sessions.
 *
 * <h2>Spring Boot Integration</h2>
 * <ul>
 *     <li>{@code @ServiceConnection}: Automatically configures the Spring application context to
 *     connect to the Ollama container.</li>
 *     <li>{@code @DynamicPropertySource}: Sets the {@code spring.ai.ollama.chat.options.model} property,
 *     ensuring that the application uses the correct model hosted in the container.</li>
 * </ul>
 * <p>
 * Test classes should extend this class to inherit the containerized Ollama setup.
 */
@Testcontainers(disabledWithoutDocker = true)
@TestPropertySource(properties = {
        "spring.ai.chat.client.enabled=false"
})
@Slf4j
public abstract class OllamaTestContainer {

    private static final String MODEL_NAME = "llama3.1:8b";
    private static final String TC_IMAGE_NAME = "internal/tc-ollama-" + MODEL_NAME;
    private static final String OLLAMA_DOCKER_IMAGE = "ollama/ollama:0.9.3";

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
