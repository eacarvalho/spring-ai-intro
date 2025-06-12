package guru.springframework.ai.service;

import guru.springframework.ai.model.Answer;
import guru.springframework.ai.model.CapitalWithInfo;
import guru.springframework.ai.model.GetCapitalRequest;
import guru.springframework.ai.model.Question;

/**
 * Created by jt, Spring Framework Guru.
 */
public interface OpenAIService {

    CapitalWithInfo getCapitalWithInfo(GetCapitalRequest getCapitalRequest);

    Answer getCapital(GetCapitalRequest getCapitalRequest);

    String getAnswer(String question);

    Answer getAnswer(Question question);
}