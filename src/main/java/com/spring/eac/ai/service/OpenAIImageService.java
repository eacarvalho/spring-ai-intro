package com.spring.eac.ai.service;

import com.spring.eac.ai.model.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAIImageService {

    private final ImageModel imageModel;
    private final ChatModel chatModel;

    public String getDescription(MultipartFile file) {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                // .model(OpenAiApi.ChatModel.GPT_4_O.getValue())
                .build();

        UserMessage userMessage = UserMessage.builder()
                .text("Explain what do you see in this picture?")
                .media(List.of(new Media(MimeTypeUtils.IMAGE_JPEG, file.getResource())))
                .build();

        Prompt userPrompt = Prompt.builder()
                .chatOptions(options)
                .messages(userMessage)
                .build();

        ChatResponse response = chatModel.call(userPrompt);

        return response.getResult().getOutput().toString();
    }

    /**
     * https://platform.openai.com/docs/guides/image-generation?image-generation-model=gpt-image-1#customize-image-output
     * @param question
     * @return
     */
    public byte[] getImage(Question question) {
        var options = OpenAiImageOptions.builder()
                // .model("dall-e-3")
                // .quality("hd") // default standard
                // .style("vivid") // default natural
                .height(1024)
                .width(1024)
                .responseFormat("b64_json")
                .build();

        ImagePrompt imagePrompt = new ImagePrompt(question.question(), options);

        var imageResponse = imageModel.call(imagePrompt);

        return Base64.getDecoder().decode(imageResponse.getResult().getOutput().getB64Json());
    }
}