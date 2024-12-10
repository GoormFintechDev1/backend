package com.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://doubly.co.kr",
                    "http://ec2-54-180-115-242.ap-northeast-2.compute.amazonaws.com:8081",
                    "http://ec2-54-180-115-242.ap-northeast-2.compute.amazonaws.com:8083",
                    "http://ec2-54-180-115-242.ap-northeast-2.compute.amazonaws.com:8084",
                    "http://localhost", "http://localhost:3000",
                    "http://localhost:3001","http://localhost:3002",
                    "http://localhost:8083",)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
