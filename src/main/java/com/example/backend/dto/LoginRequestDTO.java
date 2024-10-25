package com.example.backend.dto;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequestDTO {
    private String account;
    private String password;
}
