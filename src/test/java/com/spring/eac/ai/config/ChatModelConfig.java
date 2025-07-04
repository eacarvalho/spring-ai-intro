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
    static class OpenAiModelConfig {

        @Bean
        @ConditionalOnProperty(prefix = "spring.ai.model", name = "chat", havingValue = "openai", matchIfMissing = true)
        public ChatModel chatModel(OpenAiChatModel chatModel) {
            return chatModel;
        }

        @Bean
        @ConditionalOnProperty(prefix = "spring.ai.model", name = "embedding", havingValue = "openai", matchIfMissing = true)
        public EmbeddingModel embeddingModel(OpenAiEmbeddingModel embeddingModel) {
            return embeddingModel;
        }
    }

    @Configuration
    static class OllamaModelConfig {

        @Bean
        @ConditionalOnProperty(prefix = "spring.ai.model", name = "chat", havingValue = "ollama")
        public ChatModel chatModel(OllamaChatModel chatModel) {
            return chatModel;
        }

        @Bean
        @ConditionalOnProperty(prefix = "spring.ai.model", name = "embedding", havingValue = "ollama")
        public EmbeddingModel embeddingModel(OllamaEmbeddingModel embeddingModel) {
            return embeddingModel;
        }
    }
}
