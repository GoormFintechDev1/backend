package com.example.backend.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckAuthDTO {
    private String loginId;
    private String email;
}
