package com.example.backend.controller;

import com.example.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;


    // 글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<String> editPost(@PathVariable Long id) {

    }

    // 상품 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<String> showDetail(@PathVariable Long id) {

    }




    // 검색(카테고리 기반)
    @GetMapping("/search/category")
    public ResponseEntity<String> searchCategory() {

    }

}
