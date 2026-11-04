package com.example.ecomm.clients;

import com.example.ecomm.dtos.GroqChatResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

// Dedicated client for talking to Groq
@Component
public class GroqClient {

    private final RestClient restClient;

    public GroqClient(RestClient.Builder builder,
                      @Value("${groq.url}") String url,
                      @Value("${groq.api-key}") String apiKey) {
        this.restClient = builder.baseUrl(url)
                .defaultHeaders(headers -> headers.setBearerAuth(apiKey))
                .build();
    }

    @CircuitBreaker(name = "groq")
    public GroqChatResponseDto chat(Map<String, Object> body) {
        return restClient.post()
                .body(body)
                .retrieve()
                .body(GroqChatResponseDto.class);
    }
}
