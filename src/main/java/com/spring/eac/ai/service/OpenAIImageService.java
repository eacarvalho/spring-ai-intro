package com.spring.eac.ai.service;

import com.spring.eac.ai.model.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAIImageService {

    private final ImageModel imageModel;

    public byte[] getImage(Question question) {
        var options = OpenAiImageOptions.builder()
                .model("dall-e-3")
                .height(1024)
                .width(1024)
                .responseFormat("b64_json")
                .build();

        ImagePrompt imagePrompt = new ImagePrompt(question.question(), options);

        var imageResponse = imageModel.call(imagePrompt);

        return Base64.getDecoder().decode(imageResponse.getResult().getOutput().getB64Json());
    }
}