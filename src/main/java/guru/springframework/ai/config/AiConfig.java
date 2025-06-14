
package guru.springframework.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryManager;
import org.springframework.ai.chat.memory.MemoryChatMemoryAdapter;
import org.springframework.ai.chat.memory.MemoryChatMemoryProvider;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import guru.springframework.ai.service.WeatherTool;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class AiConfig {

    @Bean
    public ChatMemoryRepository chatMemory() {
        return new InMemoryChatMemoryRepository();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel,
                                 SimpleVectorStore vectorStore,
                                 ChatMemory chatMemory,
                                 WeatherTool weatherTool) {

        return ChatClient.builder(chatModel)
                .withMemory(chatMemory)
                .withRetriever(query -> vectorStore.similaritySearch(query, 5))
                .withTool(weatherTool)
                .build();
    }
}