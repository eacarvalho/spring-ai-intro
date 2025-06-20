package guru.springframework.ai.service;

import guru.springframework.ai.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Service
public class OpenAIServiceImpl implements OpenAIService {

    private static final Logger log = LoggerFactory.getLogger(OpenAIServiceImpl.class);

    private final ChatModel chatModel;
    private final VectorStore vectorStore;
    private final ToolCallbackProvider toolCallbackProvider;
    // private final SyncMcpToolCallbackProvider toolCallbackProvider;
    // private final CustomerScoreService customerScoreService;

    @Value("classpath:templates/get-capital-prompt.st")
    private Resource getCapitalPrompt;

    @Value("classpath:templates/get-capital-with-info.st")
    private Resource getCapitalPromptWithInfo;

    @Value("classpath:/templates/rag-prompt-template-meta.st")
    private Resource ragPromptTemplate;

    @Value("classpath:/templates/system-message.st")
    private Resource systemMessageTemplate;

    public OpenAIServiceImpl(ChatModel chatModel,
                             VectorStore vectorStore,
                             @Qualifier("customerScoreAndAllTools") ToolCallbackProvider toolCallbackProvider) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
        this.toolCallbackProvider = toolCallbackProvider;
    }

//    public OpenAIServiceImpl(ChatModel chatModel,
//                             SimpleVectorStore simpleVectorStore,
//                             SyncMcpToolCallbackProvider toolCallbackProvider,
//                             CustomerScoreService customerScoreService) {
//        this.chatModel = chatModel;
//        this.simpleVectorStore = simpleVectorStore;
//        this.toolCallbackProvider = toolCallbackProvider;
//        this.customerScoreService = customerScoreService;
//    }

    @Override
    public CapitalWithInfo getCapitalWithInfo(GetCapitalRequest getCapitalRequest) {
        PromptTemplate promptTemplate = new PromptTemplate(getCapitalPromptWithInfo);
        Prompt prompt = promptTemplate.create(Map.of("stateOrCountry", getCapitalRequest.stateOrCountry()));

        // Only another test using ResponseEntity and ParameterizedTypeReference
        ResponseEntity<ChatResponse, CapitalWithInfo> chatResponse = ChatClient.create(chatModel)
                .prompt()
                .system("""
                        Do not answer any questions not related to capitals.
                        If the question is not about capitals, set the capital field to "NOT_CAPITAL_RELATED" and 
                        provide a brief explanation in the stateOrCountry field.
                        Otherwise, provide detailed information about the capital.
                        """)
                .user(prompt.getContents())
                .call()
                .responseEntity(new ParameterizedTypeReference<>() {});

        if (chatResponse.getEntity() == null ||
                "NOT_CAPITAL_RELATED".equalsIgnoreCase(chatResponse.getEntity().capital()) ||
                chatResponse.getEntity().capital().contains("not related") ||
                chatResponse.getEntity().capital().contains("Not applicable")) {

            String errorMessage = String.format(
                    "The query '%s' is not about capitals. Please provide a query related to capital cities.",
                    getCapitalRequest.stateOrCountry());

            throw new IllegalArgumentException(errorMessage);
        }

        log.info("Got typed response: {}, Usage: {}", chatResponse.getEntity().toFormattedString(), chatResponse.getResponse().getMetadata().getUsage());

        return ChatClient.create(chatModel)
                .prompt()
                .system("Provide ONLY a JSON response in the format \\{\"capital\n\": \"capital_name\"\\} with no additional text, formatting, or code blocks")
                .user(prompt.getContents())
                .call()
                .entity(CapitalWithInfo.class);
    }

    @Override
    public GetCapitalResponse getCapital(GetCapitalRequest getCapitalRequest) {
        BeanOutputConverter<GetCapitalResponse> parser = new BeanOutputConverter<>(GetCapitalResponse.class);
        /**
         * Your response should be in JSON format.
         * Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
         * Do not include markdown code blocks in your response.
         * Remove the ```json markdown from the output.
         * Here is the JSON Schema instance your output must adhere to:
         * ```{
         *   "$schema" : "https://json-schema.org/draft/2020-12/schema",
         *   "type" : "object",
         *   "properties" : {
         *     "answer" : {
         *       "type" : "string",
         *       "description" : "This is the city name"
         *     }
         *   },
         *   "additionalProperties" : false
         * }```
         * */
        String format = parser.getFormat();

        log.info("JSON Schema response: {}", format);

        PromptTemplate promptTemplate = new PromptTemplate(getCapitalPrompt);
        Prompt prompt = promptTemplate.create(Map.of(
                "stateOrCountry", getCapitalRequest.stateOrCountry(),
                "format", format));
        ChatResponse response = chatModel.call(prompt);

        String responseText = response.getResult().getOutput().getText();

        log.info("Got response: {}", responseText);

        return parser.convert(responseText);
    }

    @Override
    public Answer getAnswer(Question question) {
        PromptTemplate systemPromptTemplate = new PromptTemplate(systemMessageTemplate);
        Prompt systemPrompt = systemPromptTemplate.create();

        SearchRequest searchRequest = SearchRequest.builder().query(question.question()).topK(4).similarityThreshold(0.2).build();
        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        List<String> contentList = documents.stream().map(Document::getText).toList();

        PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
        Prompt userPrompt = promptTemplate.create(Map.of(
                "input", question.question(),
                "documents", String.join("\n", contentList)));

//        contentList.forEach(doc -> log.info("Document: {}", doc));

        ChatResponse response = chatModel
                .call(new Prompt(
                        asList(systemPrompt.getSystemMessage(), userPrompt.getUserMessage())
                ));

        return new Answer(response.getResult().getOutput().getText());
    }

    @Override
    public String getAnswer(String question) {
        PromptTemplate promptTemplate = new PromptTemplate(question);
        Prompt prompt = promptTemplate.create();

        ChatResponse response = chatModel.call(prompt);

        return response.getResult().getOutput().getText();
    }

    /**
     * https://github.com/spring-projects/spring-ai-examples/blob/main/model-context-protocol/brave/src/main/java/org/springframework/ai/mcp/samples/brave/Application.java
     *
     * @param question
     * @return
     */
    @Override
    public Answer search(Question question) {
        ChatClient chatClient = ChatClient
                .builder(chatModel)
                .defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
                // .defaultTools(new CustomerScoreService())
                .build();

        ResponseEntity<ChatResponse, Answer> response = chatClient
                .prompt(question.question())
                .system("""
                        When using the tools for Customer Score and the customer does not exist, return:
                        Customer does not exist or does not have score.
                        """)
                .call()
                .responseEntity(new ParameterizedTypeReference<>() {
                });

        return response.getEntity();
    }
}
