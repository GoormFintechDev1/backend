package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// 비동기 방식으로 외부 API를 호출
// chatGPT API 호출 시 사용
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return WebClient.builder().baseUrl("http://localhost:8084").build();
    }

}
