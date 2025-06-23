package com.spring.eac.ai.service;

import com.spring.eac.ai.function.WeatherServiceFunction;
import com.spring.eac.ai.model.Answer;
import com.spring.eac.ai.model.Question;
import com.spring.eac.ai.model.WeatherRequest;
import com.spring.eac.ai.model.WeatherResponse;
import com.spring.eac.ai.property.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherAIService {

    private final ChatModel chatModel;
    private final ApplicationProperties applicationProperties;

    public Answer getWeather(Question question) {
        var ninjasApiKey = applicationProperties.getNinjasApiKey();
        var jsonSchema = ModelOptionsUtils.getJsonSchema(WeatherRequest.class, false);
        FunctionToolCallback<WeatherRequest, WeatherResponse> functionToolCallback = FunctionToolCallback
                .builder("CurrentWeather", new WeatherServiceFunction(ninjasApiKey))
                .description("Get the current weather for a location")
                .inputType(WeatherRequest.class)
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
                        Asking for weather or temperature of a city, state or country first get the coordinates 
                        latitude and longitude in order to call the Weather API and return all the details available.
                        """)
                .call()
                .chatResponse();

        return new Answer(response.getResult().getOutput().getText());
    }
}