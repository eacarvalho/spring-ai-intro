package com.spring.eac.ai.config;

import com.github.dockerjava.api.model.Image;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.ContainerFetchException;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.List;

@Slf4j
public class InternalOllamaContainer extends OllamaContainer {

    public static final String MODEL_NAME = "llama3.1:8b";
    public static final String TC_IMAGE_NAME = "tc-" + MODEL_NAME;
    public static final String OLLAMA_VERSION = "0.9.2"; //0.9.2;0.1.48
    public static final String OLLAMA_DOCKER_IMAGE = "ollama/ollama:" + OLLAMA_VERSION;

    public InternalOllamaContainer() {
        super(DockerImageName.parse(TC_IMAGE_NAME).asCompatibleSubstituteFor(OLLAMA_DOCKER_IMAGE));
    }

    public void createImage(String imageName) {
        OllamaContainer ollama = new OllamaContainer(OLLAMA_DOCKER_IMAGE);
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
        List<Image> listImagesCmd = DockerClientFactory.lazyClient()
                .listImagesCmd()
                .withImageNameFilter(TC_IMAGE_NAME)
                .exec();

        if (listImagesCmd.isEmpty()) {
            createImage(TC_IMAGE_NAME);
        } else {
            log.info("Using existing Ollama container with {}} image", TC_IMAGE_NAME);
        }

        super.start();
    }
}
