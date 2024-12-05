package com.example.backend.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class TokenService {


    // 레디스 관련 서비스임.
    private final RedisTemplate<String, String> redisTemplate;

    public TokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 레디스에 리프레시 토큰 저장
    public void saveRefreshToken(String loginId, String refreshToken) {
    redisTemplate.opsForValue().set("RT:" + loginId, refreshToken, Duration.ofDays(7));
    log.info("Redis에 리프레시 토큰 저장: key={}, refreshToken={}", "RT:" + loginId, refreshToken);

    }

    // 레디스에서 리프레시 토큰 꺼내기
    public String getRefreshToken(String loginId) {
        return redisTemplate.opsForValue().get("RT:" + loginId);
    }

    // 리프레시 토큰 삭제
    public void deleteRefreshToken(String loginId) {
        boolean status = redisTemplate.delete("RT:" + loginId);
        log.info("삭제 상태 --> " + status);
    }
}


//refresh token이 유효할 겨우 새로운 access token을 발급하고, redis를 통해 refresh token 관리