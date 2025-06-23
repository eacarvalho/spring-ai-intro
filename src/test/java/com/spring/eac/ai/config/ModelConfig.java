package com.spring.eac.ai.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ModelConfig {

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
