package com.spring.eac.ai.controller;

import com.spring.eac.ai.model.*;
import com.spring.eac.ai.service.OpenAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class QuestionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
    private final OpenAIService openAIService;

    public QuestionController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/capitalWithInfo")
    public CapitalWithInfo getCapitalWithInfo(@RequestBody GetCapitalRequest getCapitalRequest) {
        return this.openAIService.getCapitalWithInfo(getCapitalRequest);
    }

    @PostMapping("/capital")
    public GetCapitalResponse getCapital(@RequestBody GetCapitalRequest getCapitalRequest) {
        return this.openAIService.getCapital(getCapitalRequest);
    }

    @PostMapping("/ask")
    public Answer askQuestion(@RequestBody Question question) {
        log.info("Received question: {}", question);
        Answer answer = openAIService.getAnswer(question);
        log.info("Returning answer: {}", answer);
        return answer;
    }

    @PostMapping("/search")
    public Answer search(@RequestBody Question question) {
        log.info("Received question: {}", question);
        Answer answer = openAIService.search(question);
        log.info("Returning answer: {}", answer);
        return answer;
    }
}
