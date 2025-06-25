package com.spring.eac.ai.controller;

import com.spring.eac.ai.model.Question;
import com.spring.eac.ai.service.OpenAIImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final OpenAIImageService openAIImageService;

    @PostMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getImage(@RequestBody Question question) {
        log.info("Received question: {}", question);
        byte[] image = openAIImageService.getImage(question);
        log.info("Returning image: {} bytes", image == null ? "null" : image.length);
        return image;
    }
}