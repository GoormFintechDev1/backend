package com.example.backend.dto;

import com.example.backend.model.enumSet.PostStatusEnum;
import com.example.backend.model.enumSet.SellingAreaEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO {
    private Long id;
    private String title;
    private String description;
    private PostStatusEnum postStatus;
    private SellingAreaEnum sellingArea;
    private BigInteger price;
    private BigInteger viewCount;
    private BigInteger likeCount;
    private LocalDateTime createdAt;
    private String categoryName;
    private String memberNickname;
}
