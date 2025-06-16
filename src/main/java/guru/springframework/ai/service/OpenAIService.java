package guru.springframework.ai.service;

import guru.springframework.ai.model.*;

/**
 * Created by jt, Spring Framework Guru.
 */
public interface OpenAIService {

    CapitalWithInfo getCapitalWithInfo(GetCapitalRequest getCapitalRequest);

    GetCapitalResponse getCapital(GetCapitalRequest getCapitalRequest);

    String getAnswer(String question);

    Answer getAnswer(Question question);

    Answer search(Question question);
}