package guru.springframework.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityGuideService {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public String answer(String convId, String userText) {
        return chatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, convId))
                .user(userText)
                .call()
                .content();
    }
}