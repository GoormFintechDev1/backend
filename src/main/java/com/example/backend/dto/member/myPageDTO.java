package com.example.backend.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class myPageDTO {
    /////////// member
    private String loginId;
    // 이름
    private String name;
    // 전화번호
    private String phoneNumber;
    // 이메일
    private String email;


    /////////// business_registration
    // 사업자 등록번호
    private String brNum;
    // 사업장 주소
    private String address;
    // 사업 시작일
    private LocalDate businessStartDate;
    // 상호
    private String companyName;

}
