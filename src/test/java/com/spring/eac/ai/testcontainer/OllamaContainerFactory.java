package com.spring.eac.ai.testcontainer;

import com.github.dockerjava.api.model.Image;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.List;

/**
 * Utility class for creating and managing Ollama test containers.
 */
@Slf4j
public class OllamaContainerFactory {

    /**
     * REPOSITORY     TAG     CREATED         SIZE
     * ollama/ollama  0.9.2   5 days ago      7.34GB
     * ollama/ollama  0.3.6   10 months ago   1.1GB
     */
    private static final String OLLAMA_DOCKER_IMAGE = "ollama/ollama:0.3.6";

    public static OllamaContainer createContainer(String modelName) {
        String imageName = "internal/tc-ollama-" + modelName;

        List<Image> listImagesCmd = DockerClientFactory
                .lazyClient()
                .listImagesCmd()
                .withImageNameFilter(imageName)
                .exec();

        if (listImagesCmd.isEmpty()) {
            createImage(modelName, imageName);
        } else {
            log.info("Using existing Ollama container with {} image...", modelName);
        }

        return new OllamaContainer(DockerImageName.parse(imageName)
                .asCompatibleSubstituteFor(OLLAMA_DOCKER_IMAGE))
                .withReuse(true);
    }

    private static void createImage(String modelName, String imageName) {
        try {
            log.info("Creating a new Ollama container with {} image...", modelName);
            OllamaContainer ollama = new OllamaContainer(OLLAMA_DOCKER_IMAGE);
            ollama.start();

            log.info("Start pulling the '{}' generative ... would take several minutes ...", modelName);
            ollama.execInContainer("ollama", "pull", modelName);
            log.info("{} pulling completed!", modelName);

            log.info("Start committing the '{}' image ... would take several minutes ...", imageName);
            ollama.commitToImage(imageName);
            log.info("Finish committing the '{}' image!", imageName);
        } catch (IOException | InterruptedException e) {
            log.error("Failed to create Ollama container with {} image: {}", modelName, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}