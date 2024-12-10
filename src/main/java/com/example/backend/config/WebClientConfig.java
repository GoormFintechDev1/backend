package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // 공통 버퍼 크기 설정 메서드
    private WebClient.Builder configureBuilder(WebClient.Builder builder, int bufferSize) {
        return builder
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(bufferSize));
    }

    // localhost:8084에 연결하는 WebClient
    @Primary
    @Bean(name = "webClientLocal8084")
    public WebClient webClientLocal8084(WebClient.Builder builder) {
        return configureBuilder(builder, 2 * 1024 * 1024) // 2MB 버퍼 크기
                .baseUrl("http://localhost:8084")
                .build();
    }

    // ec2-54-180-115-242.ap-northeast-2.compute.amazonaws.com:8084에 연결하는 WebClient
    @Bean(name = "webClientRemote8084")
    public WebClient webClientRemote8084(WebClient.Builder builder) {
        return configureBuilder(builder, 2 * 1024 * 1024) // 2MB 버퍼 크기
                .baseUrl("http://ec2-54-180-115-242.ap-northeast-2.compute.amazonaws.com:8084")
                .build();
    }

    // localhost:8083에 연결하는 WebClient
    @Bean(name = "webClientLocal8083")
    public WebClient webClientLocal8083(WebClient.Builder builder) {
        return configureBuilder(builder, 10 * 1024 * 1024) // 10MB 버퍼 크기
                .baseUrl("http://localhost:8083")
                .build();
    }

    // ec2-54-180-115-242.ap-northeast-2.compute.amazonaws.com:8083에 연결하는 WebClient
    @Bean(name = "webClientRemote8083")
    public WebClient webClientRemote8083(WebClient.Builder builder) {
        return configureBuilder(builder, 10 * 1024 * 1024) // 10MB 버퍼 크기
                .baseUrl("http://ec2-54-180-115-242.ap-northeast-2.compute.amazonaws.com:8083")
                .build();
    }

    // localhost:8081에 연결하는 WebClient
    @Bean(name = "webClientLocal8081")
    public WebClient webClientLocal8081(WebClient.Builder builder) {
        return configureBuilder(builder, 10 * 1024 * 1024) // 10MB 버퍼 크기
                .baseUrl("http://localhost:8081")
                .build();
    }

    // ec2-54-180-115-242.ap-northeast-2.compute.amazonaws.com:8081에 연결하는 WebClient
    @Bean(name = "webClientRemote8081")
    public WebClient webClientRemote8081(WebClient.Builder builder) {
        return configureBuilder(builder, 10 * 1024 * 1024) // 10MB 버퍼 크기
                .baseUrl("http://ec2-54-180-115-242.ap-northeast-2.compute.amazonaws.com:8081")
                .build();
    }
}
