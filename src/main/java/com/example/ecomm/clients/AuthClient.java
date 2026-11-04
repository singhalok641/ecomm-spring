package com.example.ecomm.clients;

import com.example.ecomm.dtos.AuthUserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

// Dedicated client for talking to the Auth Service
@Component
public class AuthClient {
    private final RestClient restClient;

    public AuthClient(RestClient.Builder builder, @Value("${auth.service.url}") String authServiceUrl) {
        this.restClient = builder.baseUrl(authServiceUrl).build();
    }

    @Retryable(
            includes = HttpServerErrorException.class,
            maxRetries = 3,
            delay = 200,
            multiplier = 2, // 200 ms, 400ms, 800ms
            jitter = 100 // 200 ms +- 100ms -> 100ms to 300ms ; 400ms +- 200ms (100ms * 2) sleeps between 200ms to  600ms
    )
    public AuthUserDto validateToken(String authorizationHeader) {
        return restClient.get()
                .uri("/auth/validateToken")
                .header("Authorization", authorizationHeader)
                .retrieve()
                .body(AuthUserDto.class);
    }
}

// 4xx -> HttpClientErrorException ("caller's request/token is bad")
// 5xx -> HttpServerErrorException ("the service you are calling has some issue")

/*
upstream

---------------- your service -> 12 am -> 12:30PM -> 1:30 PM -> 4PM

downstream -> SLO of 24 hours
 */

