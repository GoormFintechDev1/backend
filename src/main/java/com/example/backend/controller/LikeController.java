package com.example.backend.controller;

import com.example.backend.model.Like;
import com.example.backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
@Slf4j
public class LikeController {
    private final LikeService likeService;

    // 좋아요 누르기
    @PostMapping("/{postId}/{memberId}")
    public ResponseEntity<String> addLike(@PathVariable Long postId, @PathVariable Long memberId) {
        if (likeService.findByPostIdAndMemberId(postId, memberId).isPresent()) {
            return ResponseEntity.badRequest().body("이미 좋아요 누른 글입니다.");
        }

        Like like = new Like();
        likeService.saveLike(like);

        return ResponseEntity.ok("좋아요 누르기 성공");
    }

    // 좋아요 취소
    @PostMapping("/delete/{postId}/{memberId}")
    public ResponseEntity<String> cancelLike(@PathVariable Long postId, @PathVariable Long memberId) {
        Optional<Like> like = likeService.findByPostIdAndMemberId(postId, memberId);
        if (like.isPresent()) {
            likeService.deleteLike(like.get().getId());
            return ResponseEntity.ok("좋아요 취소 성공");
        }
        return ResponseEntity.badRequest().body("에러");
    }
}