package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private WebClient.Builder configureBuilder(WebClient.Builder builder, int bufferSize) {
        return builder
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(bufferSize)); // 공통 버퍼 크기 설정
    }

    @Primary
    @Bean(name = "webClient8084")
    public WebClient webClientFor8084(WebClient.Builder builder) {
        return configureBuilder(builder, 2 * 1024 * 1024) // 2MB 버퍼 크기 설정 (8084에 적합한 값)
                .baseUrl("http://localhost:8084")
                .build();
    }

    @Bean(name = "webClient8083")
    public WebClient webClientFor8083(WebClient.Builder builder) {
        return configureBuilder(builder, 10 * 1024 * 1024) // 10MB 버퍼 크기
                .baseUrl("http://localhost:8083")
                .build();
    }

    @Bean(name = "webClient8081")
    public WebClient webClientFor8081(WebClient.Builder builder) {
        return configureBuilder(builder, 10 * 1024 * 1024) // 10MB 버퍼 크기
                .baseUrl("http://localhost:8081")
                .build();
    }
}
