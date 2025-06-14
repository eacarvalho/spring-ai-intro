package guru.springframework.ai.service;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.ai.tool.annotation.Tool;          // 1.0 package
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherTool {

    private final RestTemplate rest = new RestTemplate();
    private final String apiKey = System.getenv("OPEN_WEATHER_KEY");

    @Tool(name = "getWeather",
            description = "Return the current temperature and sky condition in a city")
    public String getWeather(
            @Parameter(description = "City name, e.g. Amsterdam") String city) {

        String url = "https://api.openweathermap.org/data/2.5/weather?q="
                + city + "&units=metric&appid=" + apiKey;
        try {
            var json   = rest.getForObject(url, java.util.Map.class);
            var main   = (java.util.Map<?,?>) json.get("main");
            var weather= (java.util.Map<?,?>) ((java.util.List<?>)json.get("weather")).get(0);
            return "%s °C, %s".formatted(main.get("temp"), weather.get("description"));
        } catch (Exception e) {
            return "unavailable";
        }
    }
}
