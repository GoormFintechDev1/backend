package com.example.backend.dto;
import lombok.Getter;
import lombok.Setter;


// 로그인 시 필요한 정보 캡슐화하여 AuthService로 전달
@Getter @Setter
public class LoginRequestDTO {
    private String account;
    private String password;
    private String activity;
}
