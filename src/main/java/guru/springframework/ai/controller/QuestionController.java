package guru.springframework.ai.controller;

import guru.springframework.ai.model.Answer;
import guru.springframework.ai.model.GetCapitalRequest;
import guru.springframework.ai.model.Question;
import guru.springframework.ai.service.OpenAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
    private final OpenAIService openAIService;

    public QuestionController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/capitalWithInfo")
    public Answer getCapitalWithInfo(@RequestBody GetCapitalRequest getCapitalRequest) {
        return this.openAIService.getCapitalWithInfo(getCapitalRequest);
    }

    @PostMapping("/capital")
    public Answer getCapital(@RequestBody GetCapitalRequest getCapitalRequest) {
        return this.openAIService.getCapital(getCapitalRequest);
    }

    @PostMapping("/ask")
    public Answer askQuestion(@RequestBody Question question) {
        log.info("Received question: {}", question);
        Answer answer = openAIService.getAnswer(question);
        log.info("Returning answer: {}", answer);
        return answer;
    }
}
