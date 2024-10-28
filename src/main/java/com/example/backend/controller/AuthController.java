package com.example.backend.controller;

import com.example.backend.dto.LoginRequestDTO;
import com.example.backend.dto.SignupRequestDTO;
import com.example.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> singup(@RequestBody SignupRequestDTO signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.status(201).body("회원가입 성공");
    }


//    @PostMapping("/login")
//    public ResponseEntity<String> login() {
//        return;
//    }

//    @PostMapping("/logout")
//    public ResponseEntity<String> logout() {
//        return;
//    }

}
