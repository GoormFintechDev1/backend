package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.service.AuthService;
import com.example.backend.util.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final TokenProvider tokenProvider;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDTO signupRequest) {
        authService.signup(signupRequest);
        log.info(signupRequest.toString());
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        authService.login(loginRequest, response);
        return ResponseEntity.ok("로그인 성공");
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequestDTO logoutRequest, HttpServletRequest request, HttpServletResponse response) {
        authService.logout(logoutRequest, request, response);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 회원 탈퇴(비활성화)
    @PostMapping("/inactive")
    public ResponseEntity<String> inActiveMember(@RequestBody ActivityMemberRequestDTO activityMemberRequest) {
        authService.inActiveMember(activityMemberRequest);
        return ResponseEntity.ok("회원 탈퇴 성공");
    }
    // 회원 활성화
    @PostMapping("/active")
    public ResponseEntity<?> activateMember(@RequestBody ActivityMemberRequestDTO activityMemberRequest) {
        authService.activeMember(activityMemberRequest);
        return ResponseEntity.ok("회원 활성화 성공");
    }

    // 아이디 중복 확인 (true - 중복, false - 중복 아님)
    @PostMapping("/duplication/account")
    public ResponseEntity<Boolean> checkAccount(@RequestBody CheckIdRequestDTO checkIdRequest) {
        boolean isDuplicate = authService.checkAccount(checkIdRequest);
        return ResponseEntity.ok(isDuplicate);
    }

    // 닉네임 중복 확인 (true - 중복, false - 중복 아님)
    @PostMapping("/duplication/nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestBody CheckNicknameRequestDTO checkNicknameRequest) {
        boolean isDuplicate = authService.checkNickname(checkNicknameRequest);
        return ResponseEntity.ok(isDuplicate);
    }

    // 폰 번호 중복 확인 (true - 중복, false - 중복 아님)
    @PostMapping("/duplication/phone")
    public ResponseEntity<Boolean> checkPhone(@RequestBody CheckPhoneRequestDTO  checkPhoneRequest) {
        boolean isDuplicate = authService.checkPhoneNumber(checkPhoneRequest);
        return ResponseEntity.ok(isDuplicate);
    }


    // 로그인 상태 확인
    // TODO
    //  -> 이게 필요한가? 우선 개선해서 토큰 시간 띄우는걸로 변경하긴 했음.
    @GetMapping("/check-login")
    public ResponseEntity<Map<String, Object>> checkLogin(HttpServletRequest request) {
        log.info("로그인 상태 확인 요청");

        String accessToken = tokenProvider.resolveAccessToken(request);
        Map<String, Object> response = new HashMap<>();

        if (accessToken != null && tokenProvider.validateToken(accessToken)) {
            // 토큰이 유효한 경우 남은 유효 시간 확인
            Date expiration = tokenProvider.getExpirationDate(accessToken);
            long remainingTime = expiration.getTime() - new Date().getTime();

            response.put("status", "로그인 상태 유효");
            response.put("remainingTime", remainingTime);
        } else {
            // 토큰이 없거나 만료된 경우
            response.put("status", "로그인 상태 만료");
            response.put("remainingTime", 0);
        }

        return ResponseEntity.ok(response);
    }


}
