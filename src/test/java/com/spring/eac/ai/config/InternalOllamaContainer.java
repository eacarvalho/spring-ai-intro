package com.spring.eac.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.ContainerFetchException;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

@Slf4j
public class InternalOllamaContainer extends OllamaContainer {

    public static final String MODEL_NAME = "llama2:latest";
    public static final String IMAGE_NAME = "internal/" + MODEL_NAME;

    public InternalOllamaContainer() {
        super(DockerImageName.parse(IMAGE_NAME).asCompatibleSubstituteFor("ollama/ollama:0.1.48"));
    }

    public void createImage(String imageName) {
        OllamaContainer ollama = new OllamaContainer("ollama/ollama:0.1.48");
        try {
            ollama.start();
            log.info("Start pulling the '{}' generative ... would take several minutes ...", MODEL_NAME);
            ollama.execInContainer("ollama", "pull", MODEL_NAME);
            log.info("{} pulling competed!", MODEL_NAME);
        } catch (IOException | InterruptedException e) {
            throw new ContainerFetchException(e.getMessage());
        }
        log.info("Start committing the '{}' image ... would take several minutes ...", imageName);
        ollama.commitToImage(imageName);
        log.info("Finish committing the '{}' image!", imageName);
    }

    @Override
    public void start() {
        try {
            super.start();
        } catch (ContainerFetchException ex) {
            // If an image doesn't exist, create it. Later runs will reuse the image.
            createImage(super.getDockerImageName());
            super.start();
        }
    }
}
