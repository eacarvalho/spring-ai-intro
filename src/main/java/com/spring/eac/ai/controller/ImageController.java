package com.spring.eac.ai.controller;

import com.spring.eac.ai.model.Question;
import com.spring.eac.ai.service.OpenAIImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final OpenAIImageService openAIImageService;

    @PostMapping(value = "/vision", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> upload(
            @Validated @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name) {
        return ResponseEntity.ok(openAIImageService.getDescription(file));
    }

    @PostMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getImage(@RequestBody Question question) {
        log.info("Received question: {}", question);
        byte[] image = openAIImageService.getImage(question);
        log.info("Returning image: {} bytes", image == null ? "null" : image.length);
        return image;
    }
}