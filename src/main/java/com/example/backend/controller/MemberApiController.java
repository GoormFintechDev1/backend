package com.example.backend.controller;

// 회원가입, 로그인, 비밀번호 암호화
// 비밀번호는 8자리 영문 숫자
// 중복 처리 (아이디, 닉네임, 전화번호)

import com.example.backend.dto.DuplicationDTO;
import com.example.backend.dto.LoginRequestDTO;
import com.example.backend.dto.SignupRequestDTO;
import com.example.backend.model.Member;
import com.example.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberApiController {
    private final MemberService memberService;

    //// 회원가입
    //@CrossOrigin
    //@PostMapping("/api/signup")
    //public Signup signup(SignupRequestDTO request) {
    //    Signup signup = new Signup();

    //}

    //// 로그인
    //@PostMapping("/api/login")
    //public Member login(LoginRequestDTO request) {}


    //// 로그아웃
    //@GetMapping("/api/logout")
    //public String logout() {}


    //// 회원 탈퇴

    //// 중복 확인
    // 아이디 중복 확인
    //@PostMapping("/api/duplication/id")
    //public DuplicationDTO checkAccount()



    // 닉네임 중복 확인
   // @PostMapping("/api/duplication/nickname")


    // 폰 번호 중복 확인
    //@PostMapping("/api/duplication/phone")



    // 비밀번호 확인



    //// 정보 수정하기
    /*@PutMapping("/api/modify/nickname")
    @PutMapping("/api/modify/phone")
    @PutMapping("/api/modify/address")*/




}



