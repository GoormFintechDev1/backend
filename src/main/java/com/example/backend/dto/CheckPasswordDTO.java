package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckPasswordDTO{
    private Long memberId;
    private String password;
}

