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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    // 회원가입
    public void signup(SignupRequestDTO signupRequest) {
        // 1. 중복 검사
        if (memberRepository.findByAccount(signupRequest.getAccount()).isPresent()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 아이디는 이미 사용 중입니다");
            return;
        }
        if (memberRepository.findByNickname(signupRequest.getNickname()).isPresent()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 닉네임은 이미 사용 중입니다");
            return;
        }
        if (memberRepository.findByPhoneNumber(signupRequest.getPhoneNumber()).isPresent()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 휴대번호는 이미 사용 중입니다");
            return;
        }

        // 2. 새로운 Member 객체 생성 및 정보 설정
        Member member = Member.builder()
                .account(signupRequest.getAccount())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .name(signupRequest.getName())
                .nickname(signupRequest.getNickname())
                .phoneNumber(signupRequest.getPhoneNumber())
                .address(signupRequest.getAddress())
                .build();

        // 3. DB에 저장
        memberRepository.save(member);
    }

    // 로그인
    public String login(LoginRequestDTO loginRequest, HttpServletResponse response) {
        // 1. 계정 조회 및 예외 처리
        Member member = memberRepository.findByAccount(loginRequest.getAccount())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 계정"));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호");
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


    // 로그아웃
    public void logout(String account, HttpServletResponse response) {
        // 1. 레디스에서 리프레시 토큰 삭제
        tokenService.deleteRefreshToken(account);
        // 2. 액세스 토큰 쿠키 삭제
        Cookie accessTokenCookie = new Cookie("accessToken", null); // 쿠키의 값을 null로 설정
        accessTokenCookie.setMaxAge(0); // 쿠키 만료 시간 0으로 설정
        accessTokenCookie.setPath("/"); // 해당 쿠키의 경로 설정
        accessTokenCookie.setHttpOnly(true); // HTTP 전송만 허용 (선택)
        response.addCookie(accessTokenCookie);
    }

    // 회원 탈퇴



    //// 중복 확인
    // 아이디 중복 확인




    // 닉네임 중복 확인


    // 폰 번호 중복 확인




}

