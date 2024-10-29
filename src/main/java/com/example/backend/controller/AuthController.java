package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

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
    public ResponseEntity<String> logout(@RequestBody LogoutRequestDTO logoutRequest, HttpServletResponse response) {
        authService.logout(logoutRequest, response);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 회원 탈퇴(비활성화)
    @DeleteMapping("/inactive")
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
    @PostMapping("/duplication/id")
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

    // 회원 현재 상태 로그인 중인지 아닌지 -> 구현 (상태관리)
    // 토큰으로 검증 // 유효 검증
    // 로그인 상태 확인
    @GetMapping("/check-login")
    public ResponseEntity<String> checkLogin() {
        log.info("로그인 상태 확인 요청");
        return ResponseEntity.ok("로그인 상태 유효");
    }
}
