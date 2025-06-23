package com.spring.eac.ai.service;

import com.spring.eac.ai.function.StockPriceServiceFunction;
import com.spring.eac.ai.model.Answer;
import com.spring.eac.ai.model.Question;
import com.spring.eac.ai.model.StockPriceRequest;
import com.spring.eac.ai.model.StockPriceResponse;
import com.spring.eac.ai.property.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockPriceAIService {

    private final ChatModel chatModel;
    private final ApplicationProperties applicationProperties;

    public Answer getStockPrice(Question question) {
        var ninjasApiKey = applicationProperties.getNinjasApiKey();
        var jsonSchema = ModelOptionsUtils.getJsonSchema(StockPriceRequest.class, false);
        FunctionToolCallback<StockPriceRequest, StockPriceResponse> functionToolCallback = FunctionToolCallback
                .builder("CurrentStockPrice", new StockPriceServiceFunction(ninjasApiKey))
                .description("Get the current stock price for a stock symbol")
                .inputType(StockPriceRequest.class)
                .inputSchema(jsonSchema)
                .build();

        ChatClient chatClient = ChatClient
                .builder(chatModel)
                .defaultToolCallbacks(functionToolCallback)
                .build();

        UserMessage userMessage = new UserMessage(question.question());
        ChatResponse response = chatClient
                .prompt(userMessage.getText())
                .system("""
                        You are an AI assistant that only answers questions about stock prices.
                        When asked for a stock price of a company, get the ticker symbol to call the Stock Price API and return all available details.
                        - If the ticker is not on an American Stock Exchange, you MUST reply with: "Only America Stock Exchange is supported for now."
                        - If you are asked a question about anything other than stock prices, you MUST reply with: "I can only answer questions about stock prices."
                        """)
                .call()
                .chatResponse();

        return new Answer(response.getResult().getOutput().getText());
    }
}