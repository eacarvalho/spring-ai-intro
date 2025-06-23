package com.spring.eac.ai.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

public class ModelConfiguration {

    @Configuration
    @ConditionalOnProperty(prefix = "spring.ai.chat.client", name = "enabled", havingValue = "true", matchIfMissing = true)
    static class OpenAiModelConfig {

        private static final String CHAT_MODEL = "openAiChatModel"; // openAiChatModel;ollamaChatModel
        private static final String EMBEDDING_MODEL = "openAiEmbeddingModel"; //openAiEmbeddingModel;ollamaEmbeddingModel

        @Bean
        @Primary
        public ChatModel chatModel(@Qualifier(CHAT_MODEL) ChatModel chatModel) {
            return chatModel;
        }

        @Bean
        @Primary
        public EmbeddingModel embeddingModel(@Qualifier(EMBEDDING_MODEL) EmbeddingModel embeddingModel) {
            return embeddingModel;
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "spring.ai.chat.client", name = "enabled", havingValue = "false")
    static class OllamaModelConfig {

        private static final String CHAT_MODEL = "ollamaChatModel"; // openAiChatModel
        private static final String EMBEDDING_MODEL = "ollamaEmbeddingModel"; //openAiEmbeddingModel

        @Bean
        @Primary
        public ChatModel chatModel(@Qualifier(CHAT_MODEL) ChatModel chatModel) {
            return chatModel;
        }

        @Bean
        @Primary
        public EmbeddingModel embeddingModel(@Qualifier(EMBEDDING_MODEL) EmbeddingModel embeddingModel) {
            return embeddingModel;
        }
    }
}
