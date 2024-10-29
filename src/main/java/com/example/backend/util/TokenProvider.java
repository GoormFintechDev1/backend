package com.example.backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class TokenProvider {

    @Value("${JWT.SECRET}")
    private String secretKey;

    private byte[] keyBytes;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 15; // 15분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 7일

    // SECRET_KEY를 Base64로 디코딩하여 바이트 배열로 변환
    @PostConstruct
    public void init() {
        this.keyBytes = Base64.getDecoder().decode(secretKey);
    }

    // (공통) 토큰 생성 로직
    private String createToken(String account, long expireTime) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(account)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireTime))
                .signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS512)
                .compact();
    }

    // 액세스 토큰 생성 로직
    public String createAccessToken(String account) {
        log.info("Creating Access Token for account: {}", account);
        return createToken(account, ACCESS_TOKEN_EXPIRE_TIME);
    }

    // 리프레시 토큰 생성 로직
    public String createRefreshToken(String account) {
        log.info("Creating Refresh Token for account: {}", account);
        return createToken(account, REFRESH_TOKEN_EXPIRE_TIME);
    }

    // 토큰에서 account 추출
    public String getAccountFromToken(String token) {
        log.info("Extracting account from token");
        return Jwts.parserBuilder()
                .setSigningKey(keyBytes)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(keyBytes).build().parseClaimsJws(token);
            log.info("Token is valid");
            return true; // 유효한 토큰이면 true 반환
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact of handler are invalid.");
        }
        return false; // 유효하지 않은 토큰이면 false 반환
    }

}
