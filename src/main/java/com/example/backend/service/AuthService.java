package com.example.backend.service;

import com.example.backend.dto.LoginRequestDTO;
import com.example.backend.dto.SignupRequestDTO;
import com.example.backend.model.Member;
import com.example.backend.repository.MemberRepository;
import com.example.backend.util.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    // 공통로직으로 빼서 중복조회 하기.

    // 회원가입
    public void signup(SignupRequestDTO signupRequest) {
        // 1. 계정 중복 검사 (이미 존재하는 계정인지)
        if (memberRepository.findByAccount(signupRequest.getAccount()).isPresent()) {
            throw new IllegalArgumentException("회원ID가 이미 존재합니다.");
        }

        // 2. 새로운 Member 객체 생성 및 정보 설정
        Member member = Member.builder()
                .account(signupRequest.getAccount())
                        .password(passwordEncoder.encode(signupRequest.getPassword()))


        // 이런식으로 추가하면 됩니당!

        // 3. DB에 저장
        memberRepository.save(member);
    }

    public String login(LoginRequestDTO loginRequest, HttpServletResponse response) {
        // 1. 계정 조회 및 예외 처리
        Member member = memberRepository.findByAccount(loginRequest.getAccount())
                .orElseThrow(() -> new IllegalArgumentException("Invalid account"));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // 3. 액세스 토큰, 리프레시 토큰 생성
        String accessToken = tokenProvider.createAccessToken(member.getAccount());
        String refreshToken = tokenProvider.createRefreshToken(member.getAccount());

        // 4. 리프레시 토큰 Redis에 저장
        tokenService.saveRefreshToken(member.getAccount(), refreshToken);

        // 5. 액세스 토큰을 쿠키에 저장
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(15 * 60);
        response.addCookie(accessTokenCookie);

        return refreshToken;
    }

    public void logout(String account, HttpServletResponse response) {
        // 1. 레디스에서 리프레시 토큰 삭제
        tokenService.deleteRefreshToken(account);

        // 쿠키에서 액세스 토큰 삭제하는 로직
    }

}

