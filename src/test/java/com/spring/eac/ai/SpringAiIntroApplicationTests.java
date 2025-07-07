package com.spring.eac.ai;

import com.spring.eac.ai.advisor.ReReadingAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterizedTypeReference;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class SpringAiIntroApplicationTests {

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Autowired
    private ChatMemory chatMemory;

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
                .advisors(ReReadingAdvisor.builder().build())
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
    void fluxResponseWithBlock_shouldReturnInformation() {
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

    /**
     * Based on: https://github.com/spring-projects/spring-ai/blob/main/models/spring-ai-openai/src/test/java/org/springframework/ai/openai/chat/OpenAiChatModelIT.java
     *
     * @throws InterruptedException
     */
    @Test
    @Disabled("Disabling this test class temporarily")
    @DisplayName("Flux streaming response returns a flux response using streaming")
    void fluxResponse_shouldReturnInformation() throws InterruptedException {
        ChatClient chatClient = chatClientBuilder.build();
        UserMessage userMessage = new UserMessage("List ALL natural numbers in range [1, 20]. Make sure to not omit any.");
        Prompt prompt = new Prompt(userMessage);

        StringBuilder answer = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);

        Flux<ChatResponse> chatResponseFlux = chatClient
                .prompt(prompt)
                .stream()
                .chatResponse()
                .doOnNext(chatResponse -> {
                    String responseContent = chatResponse.getResult().getOutput().getText();
                    answer.append(responseContent);
                    // log.info("Answer (stream): {}", answer);
                })
                .doOnComplete(() -> {
                    log.info("Final Answer: {}", answer.toString());
                    latch.countDown();
                });

        chatResponseFlux.subscribe();

        assertThat(latch.await(15, TimeUnit.SECONDS)).isTrue();
        IntStream.rangeClosed(1, 20).forEach(n -> assertThat(answer).contains(String.valueOf(n)));
    }

    @Test
    @Disabled("Disabling this test class temporarily")
    @DisplayName("Chat memory recalls user's name when asked in a later message")
    void chatMemory_shouldReturnInformation() {
        String conversationId = UUID.randomUUID().toString();
        ChatClient chatClient = chatClientBuilder.build();
        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor
                .builder(chatMemory)
                .conversationId(conversationId)
                .order(Ordered.HIGHEST_PRECEDENCE)
                .build();

        UserMessage userMessage1 = new UserMessage("My name is Eduardo");

        ChatResponse response1 = chatClient
                .prompt()
                .messages(userMessage1)
                .advisors(messageChatMemoryAdvisor)
                .call()
                .chatResponse();

        assertThat(response1).isNotNull();
        assertEquals(2, chatMemory.get(conversationId).size());

        UserMessage userMessage2 = new UserMessage("What is my name?");

        ChatResponse response2 = chatClient
                .prompt()
                .messages(userMessage2)
                .advisors(messageChatMemoryAdvisor)
                .call()
                .chatResponse();

        assertThat(response2).isNotNull();
        assertThat(response2.getResults()).hasSize(1);
        assertThat(response2.getResult().getOutput().getText()).contains("Eduardo");
        assertEquals(4, chatMemory.get(conversationId).size());
    }

    record ActorFilms(String actor, List<String> movies) {
    }
}