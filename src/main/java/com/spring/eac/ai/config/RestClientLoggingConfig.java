package com.spring.eac.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.http.client.HttpClientProperties;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Simple and robust way to log raw HTTP requests and responses for all OpenAI API calls using Log4j in a Spring AI 1.0.0 project.
 * The recommended approach is to implement a custom ClientHttpRequestInterceptor that uses your preferred logging
 * framework and then register this interceptor with the RestClient used by Spring AI.
 */
@Configuration
public class RestClientLoggingConfig {

    /**
     * Customizes the RestClient to add a logging interceptor.
     */
    @Bean
    public RestClientCustomizer restClientCustomizer(HttpClientProperties httpClientProperties) {
        return restClientBuilder -> restClientBuilder.requestInterceptor(new ClientLoggerRequestInterceptor());
    }

    /**
     * Interceptor for logging HTTP requests and responses.
     */
    @Slf4j
    public static class ClientLoggerRequestInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {

            long start = System.currentTimeMillis();

            logRequest(request, body);

            ClientHttpResponse response;
            try {
                response = execution.execute(request, body);
            } catch (Exception ex) {
                log.error("Request failed: {} {} Headers: {}",
                        request.getMethod(), request.getURI(), request.getHeaders(), ex);
                throw ex;
            }

            long duration = System.currentTimeMillis() - start;

            return logResponse(response, duration);
        }

        private void logRequest(HttpRequest request, byte[] body) {
            log.info("Request: {} {}", request.getMethod(), request.getURI());
            log.debug("Request headers: {}", request.getHeaders());
            if (body != null && body.length > 0) {
                log.debug("Request body: {}", new String(body, StandardCharsets.UTF_8));
            }
        }

        private ClientHttpResponse logResponse(ClientHttpResponse response,
                                               long duration) throws IOException {
            log.info("Response status: {} ({} ms)", response.getStatusCode(), duration);
            log.debug("Response headers: {}", response.getHeaders());
            byte[] responseBody = response.getBody().readAllBytes();
            if (responseBody.length > 0) {
                log.debug("Response body: {}", new String(responseBody, StandardCharsets.UTF_8));
            }
            // Return wrapped response to allow reading the body again
            return new BufferingClientHttpResponseWrapper(response, responseBody);
        }
    }

    /**
     * Wrapper to buffer the response body for multiple reads.
     */
    private static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {
        private final ClientHttpResponse response;
        private final byte[] body;

        public BufferingClientHttpResponseWrapper(ClientHttpResponse response, byte[] body) {
            this.response = response;
            this.body = body;
        }

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return response.getStatusCode();
        }

        @Override
        public HttpHeaders getHeaders() {
            return response.getHeaders();
        }

        @Override
        public void close() {
            response.close();
        }

        @Override
        public String getStatusText() throws IOException {
            return response.getStatusText();
        }
    }
}
