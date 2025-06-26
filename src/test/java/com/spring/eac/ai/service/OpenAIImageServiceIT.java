package com.spring.eac.ai.service;

import com.spring.eac.ai.model.Question;
import com.spring.eac.ai.testcontainer.LlavaTestContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Disabling this test class temporarily")
@SpringBootTest
@Slf4j
class OpenAIImageServiceIT extends LlavaTestContainer {

    @Autowired
    private OpenAIImageService imageService;

    @Test
    @DisplayName("Given valid football image, when getDescription called, then returns non-empty description")
    void getDescription_validImage_returnsDescription() throws Exception {
        // Given: Load football image from test resources
        Resource imageResource = new ClassPathResource("images/Football_ball.jpg");
        MultipartFile validImage = new MockMultipartFile(
                "football.jpg",
                "football.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                imageResource.getInputStream()
        );

        // When
        String description = imageService.getDescription(validImage);

        // Then
        assertThat(description).isNotBlank();
        assertTrue(description.toLowerCase().contains("football") ||
                description.toLowerCase().contains("soccer"));
    }

    @Test
    @DisplayName("Given invalid file type, when getDescription called, then throws exception")
    void getDescription_invalidFile_throwsException() {
        // Given
        MultipartFile invalidFile = new MockMultipartFile(
                "text.txt",
                "text.txt",
                "text/plain",
                "Invalid content".getBytes(StandardCharsets.UTF_8)
        );

        // When & Then
        assertThrows(Exception.class, () ->
                imageService.getDescription(invalidFile)
        );
    }

    @Test
    @DisplayName("Given valid prompt, when getImage called, then returns non-empty image data")
    void getImage_validPrompt_returnsImage() throws Exception {
        // Given
        Question validQuestion = new Question("A red balloon floating in the sky");

        // When
        byte[] imageData = imageService.getImage(validQuestion);

        // Then
        assertThat(imageData).isNotEmpty();
        assertTrue(imageData.length > 1000);

        // Save to disk for inspection (e.g., in target/test-output/)
        Path outputPath = Paths.get("target/test-output/red_balloon.png");
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, imageData);
        log.info("Image saved to: {}", outputPath.toAbsolutePath());
    }

    @Test
    @DisplayName("Given empty prompt, when getImage called, then throws exception")
    void getImage_emptyPrompt_throwsException() {
        // Given
        Question invalidQuestion = new Question("");

        // When & Then
        assertThrows(Exception.class, () ->
                imageService.getImage(invalidQuestion)
        );
    }
}