package com.example.backend.config;

import com.example.backend.filter.JwtAuthenticationFilter;
import com.example.backend.util.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    public SecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("Configuring PasswordEncoder as BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring SecurityFilterChain with JWT Authentication Filter");

        http.csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // 인증 및 회원가입 엔드포인트 접근 허용
                        .requestMatchers("/api/member/**").authenticated() // 회원 정보 수정은 인증된 사용자만 접근
                        .anyRequest().authenticated()  // 나머지 엔드포인트는 인증 필요
                )
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

        log.info("SecurityFilterChain configured successfully");
        return http.build();
    }
}
