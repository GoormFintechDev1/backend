package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDTO {
    private String account; // 중복 확인
    private String password;
    private String name;
    private String nickname; // 중복 확인
    private String phoneNumber; // 중복 확인
    private String address;
}






