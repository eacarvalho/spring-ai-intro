package com.spring.eac.ai.advisor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * Re-Reading (Re2) Advisor
 * The "Re-Reading Improves Reasoning in Large Language Models" article introduces a technique called Re-Reading (Re2)
 * that improves the reasoning capabilities of Large Language Models. The Re2 technique requires augmenting
 * the input prompt like this:
 * https://arxiv.org/pdf/2309.06275
 * <p>
 * {Input_Query}
 * Read the question again: {Input_Query}
 */
@AllArgsConstructor
@Slf4j
public class ReReadingAdvisor implements CallAdvisor, StreamAdvisor {

    private final int order;

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        ChatClientRequest modifiedRequest = this.before(chatClientRequest);
        ChatClientResponse response = callAdvisorChain.nextCall(modifiedRequest);

        if (response.chatResponse() != null) {
            log.info("Model response: {}", response.chatResponse().getResult().getOutput().getText());
        } else {
            log.warn("Model response is null or empty.");
        }

        return response;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        ChatClientRequest modifiedRequest = this.before(chatClientRequest);

        return streamAdvisorChain.nextStream(modifiedRequest)
                .doOnNext(response -> {
                    if (response.chatResponse() != null) {
                        log.info("Model streaming response: {}", response.chatResponse().getResult().getOutput().getText());
                    } else {
                        log.warn("Model streaming response is null or empty.");
                    }
                });
    }

    private ChatClientRequest before(ChatClientRequest chatClientRequest) {
        log.info("Original prompt: {}", chatClientRequest.prompt().getUserMessage().getText());

        Map<String, Object> advisedUserParams = new HashMap<>(chatClientRequest.context());
        advisedUserParams.put("re2_input_query", chatClientRequest.prompt().getUserMessage().getText());

        PromptTemplate promptTemplate = new PromptTemplate("""
                {re2_input_query}
                Read the question again: {re2_input_query}
                """);
        Prompt prompt = promptTemplate
                .create(Map.of("re2_input_query", advisedUserParams.get("re2_input_query")));

        ChatClientRequest modifiedRequest = ChatClientRequest.builder()
                .prompt(prompt)
                .context(advisedUserParams)
                .build();

        log.info("Reasoning (modified prompt): {}", modifiedRequest.prompt().getUserMessage().getText());

        return modifiedRequest;
    }

    public static ReReadingAdvisor.Builder builder() {
        return new ReReadingAdvisor.Builder();
    }

    public static final class Builder {
        private int order = 0;

        private Builder() {
        }

        public ReReadingAdvisor.Builder order(int order) {
            this.order = order;
            return this;
        }

        public ReReadingAdvisor build() {
            return new ReReadingAdvisor(this.order);
        }
    }
}