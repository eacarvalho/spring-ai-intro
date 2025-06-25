package com.spring.eac.ai.controller;

import com.spring.eac.ai.model.*;
import com.spring.eac.ai.service.OpenAIChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class QuestionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
    private final OpenAIChatService openAIChatService;

    @PostMapping("/capitalWithInfo")
    public CapitalWithInfo getCapitalWithInfo(@RequestBody GetCapitalRequest getCapitalRequest) {
        return this.openAIChatService.getCapitalWithInfo(getCapitalRequest);
    }

    @PostMapping("/capital")
    public GetCapitalResponse getCapital(@RequestBody GetCapitalRequest getCapitalRequest) {
        return this.openAIChatService.getCapital(getCapitalRequest);
    }

    @PostMapping("/ask")
    public Answer askQuestion(@RequestBody Question question) {
        log.info("Received question: {}", question);
        Answer answer = openAIChatService.getAnswer(question);
        log.info("Returning answer: {}", answer);
        return answer;
    }

    @PostMapping("/search")
    public Answer search(@RequestBody Question question) {
        log.info("Received question: {}", question);
        Answer answer = openAIChatService.search(question);
        log.info("Returning answer: {}", answer);
        return answer;
    }
}
