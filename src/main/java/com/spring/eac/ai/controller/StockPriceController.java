package com.spring.eac.ai.controller;

import com.spring.eac.ai.model.Answer;
import com.spring.eac.ai.model.Question;
import com.spring.eac.ai.service.StockPriceAIService;
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
public class StockPriceController {

    private static final Logger log = LoggerFactory.getLogger(StockPriceController.class);
    private final StockPriceAIService stockPriceAIService;

    @PostMapping("/stockprice")
    public Answer stockprice(@RequestBody Question question) {
        log.info("Received question: {}", question);
        Answer answer = stockPriceAIService.getStockPrice(question);
        log.info("Returning answer: {}", answer);
        return answer;
    }
}
