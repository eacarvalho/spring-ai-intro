package guru.springframework.ai.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerScoreTool {

    @Tool(name = "getCustomerScoreByName", description = "Get internal score details using an customer name")
    CustomerScore getCustomerScoreByName(String customerName) {
        return new CustomerScore("Eduardo Alvim", 8.9);
    }

    @Tool(name = "getCustomerScore", description = "Get highest customer score")
    List<CustomerScore> getCustomerScore() {
        return List.of(
                new CustomerScore("Eduardo Alvim", 8.9),
                new CustomerScore("Jane Doe", 9.1)
        );
    }

    record CustomerScore(String name, double score) {
    }
}