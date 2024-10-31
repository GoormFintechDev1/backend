package com.example.backend.model;

import com.example.backend.model.enumSet.PostStatusEnum;
import com.example.backend.model.enumSet.SellingAreaEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "posts")
public class Post extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 게시글 ID

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // 카테고리 (연결된 카테고리 엔티티)

    @Column(name = "member_id", nullable = false)
    private Long memberId; // 회원 식별 번호

    @Column(name = "title", length = 100, nullable = false)
    private String title; // 상품 제목

    @Enumerated(EnumType.STRING)
    @Column(name = "post_status", nullable = false)
    private PostStatusEnum postStatus; // 상품 상태 (sale, reserved, soldout)

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 상품 설명

    @Column(name = "price", nullable = false)
    private BigInteger price; // 판매가

    @Column(name = "view_count")
    private BigInteger viewCount; // 조회수

    @Enumerated(EnumType.STRING)
    @Column(name = "selling_area", nullable = false)
    private SellingAreaEnum sellingArea; // 거래 희망 장소 (판매자, 구매자, 협의)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 생성 일자

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정 일자



}
