package com.example.backend.repository;

import com.example.backend.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 내가 좋아요한 게시글 보기
    Optional<Like> findByPostIdAndMemberId(Long postId, Long memberId);
}