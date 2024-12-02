package com.example.backend.dto.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BusinessRegistrationDTO {
    // 외부 사업자에서 받아오는 DTO
    private String brNum;
    private String businessType;
    private String businessItem;
    private String address;
    private String representativeName;
    private String companyName;
}
