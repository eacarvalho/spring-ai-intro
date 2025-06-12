package guru.springframework.ai.service;

import guru.springframework.ai.model.Answer;
import guru.springframework.ai.model.CapitalWithInfo;
import guru.springframework.ai.model.GetCapitalRequest;
import guru.springframework.ai.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OpenAIServiceImpl implements OpenAIService {

    private static final Logger log = LoggerFactory.getLogger(OpenAIServiceImpl.class);

    private final ChatModel chatModel;

    @Value("classpath:templates/get-capital-prompt.st")
    private Resource getCapitalPrompt;

    @Value("classpath:templates/get-capital-with-info.st")
    private Resource getCapitalPromptWithInfo;

    public OpenAIServiceImpl(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

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
    public Answer getCapital(GetCapitalRequest getCapitalRequest) {
        BeanOutputConverter<Answer> parser = new BeanOutputConverter<>(Answer.class);
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
         *       "type" : "string"
         *     }
         *   }
         * }```
         * */
        String format = parser.getFormat();
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
        PromptTemplate promptTemplate = new PromptTemplate(question.question());
        Prompt prompt = promptTemplate.create();
        ChatResponse response = chatModel.call(prompt);

        return new Answer(response.getResult().getOutput().getText());
    }

    @Override
    public String getAnswer(String question) {
        PromptTemplate promptTemplate = new PromptTemplate(question);
        Prompt prompt = promptTemplate.create();

        ChatResponse response = chatModel.call(prompt);

        return response.getResult().getOutput().getText();
    }
}
