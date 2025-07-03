package com.spring.eac.ai;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class SpringAiIntroApplicationTests {

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Test
    @DisplayName("Context loads without errors")
    void contextLoads() {
    }

    @Test
    @Disabled("Disabling this test class temporarily")
    @DisplayName("Simple chat response returns information")
    void simpleChatResponse_shouldReturnInformation() {
        ChatClient chatClient = chatClientBuilder.build();

        ChatResponse chatResponse = chatClient.prompt()
                .user("Tell me a joke")
                .call()
                .chatResponse();

        log.info(chatResponse.toString());

        assertNotNull(chatResponse);
        assertFalse(chatResponse.getResult().getOutput().getText().isEmpty());
    }

    @Test
    @Disabled("Disabling this test class temporarily")
    @DisplayName("Structured response returns a valid actor and filmography")
    void structuredResponse_shouldReturnInformation() {
        ChatClient chatClient = chatClientBuilder.build();

        ActorFilms actorFilms = chatClient.prompt()
                .user("Generate the filmography for a random actor.")
                .call()
                .entity(ActorFilms.class);

        log.info(actorFilms.toString());

        assertNotNull(actorFilms);
        assertFalse(actorFilms.actor().isEmpty());
        assertThat(actorFilms.movies).hasSizeGreaterThan(0);
    }

    @Test
    @Disabled("Disabling this test class temporarily")
    @DisplayName("Flux streaming response returns a non-empty list of actor films")
    void fluxResponse_shouldReturnInformation() {
        ChatClient chatClient = chatClientBuilder.build();
        var converter = new BeanOutputConverter<>(new ParameterizedTypeReference<List<ActorFilms>>() {
        });

        Flux<String> flux = chatClient.prompt()
                .user(u -> u.text("""
                                  Generate the filmography for a random actor.
                                  {format}
                                """)
                        .param("format", converter.getFormat()))
                .stream()
                .content();

        String content = flux
                .collectList()
                .block()
                .stream()
                .collect(Collectors.joining());

        List<ActorFilms> actorFilms = converter.convert(content);

        log.info(actorFilms.toString());

        assertNotNull(actorFilms);
        assertTrue(actorFilms.size() > 0, "actorFilms list should not be empty");
    }

    record ActorFilms(String actor, List<String> movies) {
    }
}