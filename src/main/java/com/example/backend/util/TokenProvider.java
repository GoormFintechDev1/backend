package com.example.backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final RedisTemplate<String, String> redisTemplate;

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

    // 토큰 유효성 검증 로직
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(keyBytes).build().parseClaimsJws(token);
            log.info("토큰이 유효합니다.");
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다."); // 만료된 토큰인 경우
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("유효하지 않은 JWT 서명입니다."); // 서명 오류
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다."); // 지원되지 않는 형식
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다."); // 형식 오류 등
        }
        return false; // 유효하지 않은 토큰인 경우
    }

    // 쿠키에서 액세스 토큰 추출
    public String resolveAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // Redis에서 리프레시 토큰을 조회
    public String getRefreshTokenFromRedis(String account) {
        return (String) redisTemplate.opsForValue().get(account);
    }

    // 새로운 액세스 토큰을 쿠키에 설정
    public void setAccessTokenCookie(String accessToken, HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(15 * 60); // 15분
        response.addCookie(accessTokenCookie);
    }

    // 액세스 토큰 쿠키 만료 처리
    public void expireAccessTokenCookie(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", null); // 쿠키 값을 null로 설정
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        response.addCookie(accessTokenCookie);
    }


    public Date getExpirationDate(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(keyBytes)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }



}
