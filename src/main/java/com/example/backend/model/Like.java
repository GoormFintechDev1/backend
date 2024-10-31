package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "likes")

public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 좋아요 ID

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member memberId; // 회원 식별 번호 (연결된 회원 엔티티)

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 글 식별 번호 (연결된 글 엔티티)

}
