package com.spring.eac.ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpenAIChatServiceIT {

    @Autowired
    OpenAIChatService openAIChatService;

    @Test
    void getAnswer() {
        String answer = openAIChatService.getAnswer("Write a python script to output numbers from 1 to 100.");
        System.out.println("Got an answer");
        System.out.println(answer);
    }
}