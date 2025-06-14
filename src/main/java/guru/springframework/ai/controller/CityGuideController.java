package guru.springframework.ai.controller;

import guru.springframework.ai.service.CityGuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
class CityGuideController {

    private final CityGuideService service;

    /**
     * curl -X POST "localhost:8080/api/ask?convId=1" \
     *      -d "What should I visit in Amsterdam today?"
     * @param convId
     * @param question
     * @return
     */
    @PostMapping("/ask")
    public String ask(@RequestParam String convId,
                      @RequestBody String question) {
        return service.answer(convId, question);
    }
}