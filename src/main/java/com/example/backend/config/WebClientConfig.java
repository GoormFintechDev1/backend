package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // 8084 서버(WebClient 1)
    @Primary
    @Bean(name = "webClient8084")
    public WebClient webClientFor8084(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8084")
                .build();
    }

    // 8083 서버(WebClient 2)
    @Bean(name = "webClient8083")
    public WebClient webClientFor8083(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8083")
                .build();
    }

    // 8081 서버(WebClient 3)
    @Bean(name = "webClient8081")
    public WebClient webClientFor8081(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8081")
                .build();
    }
}
