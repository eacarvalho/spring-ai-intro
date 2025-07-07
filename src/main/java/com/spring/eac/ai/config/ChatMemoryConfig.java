package com.spring.eac.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * https://docs.spring.io/spring-ai/reference/api/chat-memory.html
 */
@Configuration
@Slf4j
public class ChatMemoryConfig {

    @Bean
    public JdbcChatMemoryRepository chatMemoryRepository(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        return JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(JdbcChatMemoryRepositoryDialect.from(dataSource))
                // .dialect(new MysqlChatMemoryRepositoryDialect())
                .build();
    }

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        try {
            // Optionally, test the connection or repository here
            jdbcChatMemoryRepository.findConversationIds(); // Throws if DB is unavailable

            log.info("Using jdbc chat memory because there is connection with Database");

            return MessageWindowChatMemory.builder()
                    .chatMemoryRepository(jdbcChatMemoryRepository)
                    .maxMessages(10)
                    .build();
        } catch (DataAccessException ex) {
            log.warn("Using in memory chat memory because there is no connection with Database");
            // Fallback to in-memory if DB is unreachable
            return MessageWindowChatMemory.builder()
                    .chatMemoryRepository(new InMemoryChatMemoryRepository())
                    .maxMessages(10)
                    .build();
        }
    }
}
