package com.spring.eac.ai.function;

import com.spring.eac.ai.model.QRCodeRequest;
import com.spring.eac.ai.model.QRCodeResponse;
import com.spring.eac.ai.property.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class QRCodeFunction {
    public static final String STOCK_URL = "https://api.api-ninjas.com/v1/qrcode";
    public static final String GENERATE_QR_CODE = "generateQRCode";

    private final RestClient restClient;

    public QRCodeFunction(ApplicationProperties applicationProperties) {
        var ninjasApiKey = applicationProperties.getNinjasApiKey();
        this.restClient = RestClient.builder()
                .baseUrl(STOCK_URL)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("X-Api-Key", ninjasApiKey);
                    httpHeaders.set("Accept", "image/png");
                    httpHeaders.set("Content-Type", "image/png");
                })
                .requestInterceptor((request, body, execution) -> {
                    log.info("Final URI: {}", request.getURI());
                    return execution.execute(request, body);
                })
                .build();
    }

    /**
     * https://docs.spring.io/spring-ai/reference/api/tools.html#_function_return_direct
     * @return
     */
    @Bean(GENERATE_QR_CODE)
    @Description("Generate a QR Code based on the input data")
    // @Tool(description = "Generate a QR Code based on the input data", returnDirect = true)
    Function<QRCodeRequest, QRCodeResponse> generateQRCode() {
        return request -> {
            // Validate input parameters
            if (request == null || request.data() == null) {
                log.error("QRCodeRequest is null");
                throw new IllegalArgumentException("Data request cannot be null");
            }

            // Make the GET request to the QR code API
            byte[] imageData = restClient.get()
                    .uri("?data={data}&format={format}", request.data(), request.format())
                    .retrieve()
                    .body(byte[].class);

            return new QRCodeResponse(imageData);
        };
    }
}