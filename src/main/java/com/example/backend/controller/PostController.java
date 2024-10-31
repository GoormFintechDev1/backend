package com.example.backend.controller;

import com.example.backend.dto.PostResponseDTO;
import com.example.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;


//
//    // 게시글 생성
//    @PostMapping
//    public ResponseEntity<Post> createPost(@RequestBody PostRequestDTO postRequestDTO) {
//        Post createdPost = postService.createPost(postRequestDTO);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
//    }
//

    // 글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<String> editPost(@PathVariable Long id) {

        return null;
    }

    // 상품 전체 조회
    @GetMapping("/all")
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        List<PostResponseDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 상품 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<String> showDetail(@PathVariable Long id) {

        return null;
    }



    // 검색(카테고리 기반)
    @GetMapping("/search/category")
    public ResponseEntity<String> searchCategory() {

        return null;
    }

    @GetMapping("/search/keyword")
    public ResponseEntity<String> searchKeyword() {

        return null;
    }


    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        return null;
    }
}
