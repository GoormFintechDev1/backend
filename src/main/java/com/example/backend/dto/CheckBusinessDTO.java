package com.example.backend.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckBusinessDTO {
    // 사업자 등록 번호
    private Long brNum;
    // 사업자 주소
    private String address;

}
