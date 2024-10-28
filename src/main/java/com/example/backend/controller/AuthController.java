package com.example.backend.controller;
import com.example.backend.dto.LoginRequestDTO;
import com.example.backend.dto.SignupRequestDTO;
import com.example.backend.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDTO signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        authService.login(loginRequest,response);
        return ResponseEntity.ok("로그인 성공");
    }

    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String account, HttpServletResponse response) {
        authService.logout(account,response);
        return ResponseEntity.ok("로그아웃 성공");
    }


//    // 회원 탈퇴
//    @GetMapping("/remove")
//    public ResponseEntity<String> remove() {
//
//    }
//
//
//    //// 중복 확인
//    // 아이디 중복 확인 (true - 중복, false - 중복 아님)
//    @PostMapping("/duplication/id")
//    public ResponseEntity<Boolean> checkAccount(){
//
//    }
//
//    // 닉네임 중복 확인 (true - 중복, false - 중복 아님)
//    @PostMapping("/duplication/nickname")
//    public ResponseEntity<Boolean> checkNickname(){
//
//    }
//
//
//    // 폰 번호 중복 확인 (true - 중복, false - 중복 아님)
//    @PostMapping("/api/duplication/phone")
//    public ResponseEntity<Boolean> checkPhone(){
//
//    }

}
