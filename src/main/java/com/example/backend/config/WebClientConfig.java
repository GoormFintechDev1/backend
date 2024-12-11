package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	
	@Value("${bank.api.url}")
    private String bankUrl;
	
	@Value("${pos.api.url}")
	private String posUrl;
	
	@Value("${br.api.url}")
	private String brUrl;

    // 공통 버퍼 크기 설정 메서드
    private WebClient.Builder configureBuilder(WebClient.Builder builder, int bufferSize) {
        return builder
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(bufferSize));
    }
  
    // localhost:8081에 연결하는 WebClient
    @Bean(name = "webClient8081")
    public WebClient webClient8081(WebClient.Builder builder) {
        return configureBuilder(builder, 10 * 1024 * 1024) // 10MB 버퍼 크기
                .baseUrl(bankUrl)
                .build();
    }
  
    // localhost:8083에 연결하는 WebClient
    @Bean(name = "webClient8083")
    public WebClient webClient8083(WebClient.Builder builder) {
        return configureBuilder(builder, 10 * 1024 * 1024) // 10MB 버퍼 크기
                .baseUrl(posUrl)
                .build();
    }

    // localhost:8084에 연결하는 WebClient
    @Primary
    @Bean(name = "webClient8084")
    public WebClient webClient8084(WebClient.Builder builder) {
        return configureBuilder(builder, 2 * 1024 * 1024) // 2MB 버퍼 크기
                .baseUrl(brUrl)
                .build();
    }
}
