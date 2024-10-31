package com.example.backend.controller;

import com.example.backend.model.Post;
import com.example.backend.dto.PostResponseDTO;
import com.example.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import java.util.List;

@RestController
@RequestMapping("/api/post")
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
    public ResponseEntity<String> editPost(@PathVariable Long postId, @RequestBody Post updatedPost) {
        boolean isUpdated = postService.editPost(postId, updatedPost);
        if (isUpdated) {
            return ResponseEntity.ok("Post updated successfully.");
        } else {
            return ResponseEntity.status(404).body("Post not found.");
        }
    }

    // 상품 전체 조회
    @GetMapping("/all")
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        List<PostResponseDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 상품 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<Post> showDetail(@PathVariable Long postId) {
        Optional<Post> post = postService.showDetail(postId);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    // 검색(카테고리 기반)
    @GetMapping("/search/category")
    public ResponseEntity<?> searchCategory(@RequestParam String category) {
        return ResponseEntity.ok(postService.searchByCategory(category));
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