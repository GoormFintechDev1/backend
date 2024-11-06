package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;


// 회원가입 시 필요한 정보 캡슐화하여 AuthService로 전달
@Getter
@Setter
public class SignupRequestDTO {
    private String loginId; // 중복 확인
    private String password;
    private String name;
    private String identityNumber;
    private String phoneNumber; // 중복 확인
}






