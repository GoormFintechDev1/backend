package com.example.backend.dto;

import com.example.backend.model.enumSet.PostStatusEnum;
import com.example.backend.model.enumSet.SellingAreaEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDTO {

    private Long categoryId;
    private Long memberId;
//    private String imageUrl; -> 추후 추가 예정.
    private String title;
    private String content;
    private String description;
    private BigInteger price;
    private PostStatusEnum postStatus;
    private SellingAreaEnum sellingArea;

}
