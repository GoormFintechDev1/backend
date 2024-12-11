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

	// 8081 서버(WebClient 3)
	@Bean(name = "webClient8081")
	public WebClient webClientFor8081(WebClient.Builder builder) {
		return builder
				.baseUrl(bankUrl)
				.build();
	}
	
    // 8083 서버(WebClient 2)
    @Bean(name = "webClient8083")
    public WebClient webClientFor8083(WebClient.Builder builder) {
        return builder
                .baseUrl(posUrl)
                .build();
    }

    // 8084 서버(WebClient 1)
    @Primary
    @Bean(name = "webClient8084")
    public WebClient webClientFor8084(WebClient.Builder builder) {
    	return builder
    			.baseUrl(brUrl)
    			.build();
    }
}
