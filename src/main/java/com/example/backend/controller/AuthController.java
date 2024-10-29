package com.example.backend.controller;

import com.example.backend.dto.LoginRequestDTO;
import com.example.backend.dto.SignupRequestDTO;
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
    public ResponseEntity<String> logout(@RequestBody Map<String, String> request, HttpServletResponse response) {
        String account = request.get("account");
        authService.logout(account, response);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 회원 탈퇴
    @DeleteMapping("/remove")
    public ResponseEntity<String> remove(@RequestBody Map<String, String> request) {
        String account = request.get("account");
        authService.removeMember(account);
        return ResponseEntity.ok("회원 탈퇴 성공");
    }

    // 아이디 중복 확인 (true - 중복, false - 중복 아님)
    @PostMapping("/duplication/id")
    public ResponseEntity<Boolean> checkAccount(@RequestBody Map<String, String> request) {
        String account = request.get("account");
        boolean isDuplicate = authService.checkAccount(account);
        return ResponseEntity.ok(isDuplicate);
    }

    // 닉네임 중복 확인 (true - 중복, false - 중복 아님)
    @PostMapping("/duplication/nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestBody Map<String, String> request) {
        String nickname = request.get("nickname");
        boolean isDuplicate = authService.checkNickname(nickname);
        return ResponseEntity.ok(isDuplicate);
    }

    // 폰 번호 중복 확인 (true - 중복, false - 중복 아님)
    @PostMapping("/duplication/phone")
    public ResponseEntity<Boolean> checkPhone(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        boolean isDuplicate = authService.checkPhoneNumber(phone);
        return ResponseEntity.ok(isDuplicate);
    }
}
