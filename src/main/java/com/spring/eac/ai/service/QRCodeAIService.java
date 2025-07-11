package com.spring.eac.ai.service;

import com.spring.eac.ai.function.QRCodeFunction;
import com.spring.eac.ai.model.QRCodeRequest;
import com.spring.eac.ai.model.QRCodeResponse;
import com.spring.eac.ai.model.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class QRCodeAIService {

    private final ChatModel chatModel;
    private final Function<QRCodeRequest, QRCodeResponse> generateQRCode;

    public byte[] generateQRCode(Question question) {
        ToolCallback qrCodeToolCallback = FunctionToolCallback
                .builder(QRCodeFunction.GENERATE_QR_CODE, generateQRCode)
                .description("Generate a QR Code based on the input data")
                .inputType(QRCodeRequest.class)
                .toolMetadata(ToolMetadata.builder().returnDirect(true).build())
                .build();

        ChatClient chatClient = ChatClient
                .builder(chatModel)
                .defaultToolCallbacks(qrCodeToolCallback)
                .build();

        ResponseEntity<ChatResponse, QRCodeResponse> chatResponse = chatClient
                .prompt(question.question())
                .call()
                .responseEntity(new ParameterizedTypeReference<>() {});

        return chatResponse.getEntity().imageData();
    }
}