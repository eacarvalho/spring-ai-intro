package com.spring.eac.ai.controller;

import com.spring.eac.ai.model.Question;
import com.spring.eac.ai.service.OpenAIAudioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class AudioController {

    private final OpenAIAudioService openAIAudioService;

    @PostMapping(value ="/talk", produces = "audio/mpeg")
    public byte[] talk(@RequestBody Question question) {
        return openAIAudioService.getSpeech(question);
    }
}