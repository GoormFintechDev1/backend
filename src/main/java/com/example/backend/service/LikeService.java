package com.example.backend.service;

import com.example.backend.model.Like;
import com.example.backend.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    // 좋아요 누르기
    public Like saveLike(Like like) {
        return likeRepository.save(like);
    }

    // 좋아요 취소하기
    public void deleteLike(Long id) {
        likeRepository.deleteById(id);
    }

    // 내가 좋아요 한 글 보기
    public Optional<Like> findByPostIdAndMemberId(Long postId, Long memberId) {
        return likeRepository.findByPostIdAndMemberId(postId, memberId);
    }
}