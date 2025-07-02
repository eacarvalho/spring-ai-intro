package com.spring.eac.ai.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class ChatModelConfig {

    @Configuration
    @ConditionalOnProperty(prefix = "spring.ai.chat.client", name = "enabled", havingValue = "true", matchIfMissing = true)
    static class OpenAiModelConfig {

        @Bean
        public ChatModel chatModel(OpenAiChatModel chatModel) {
            return chatModel;
        }

        @Bean
        public EmbeddingModel embeddingModel(OpenAiEmbeddingModel embeddingModel) {
            return embeddingModel;
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "spring.ai.chat.client", name = "enabled", havingValue = "false")
    static class OllamaModelConfig {

        @Bean
        public ChatModel chatModel(OllamaChatModel chatModel) {
            return chatModel;
        }

        @Bean
        public EmbeddingModel embeddingModel(OllamaEmbeddingModel embeddingModel) {
            return embeddingModel;
        }
    }
}
