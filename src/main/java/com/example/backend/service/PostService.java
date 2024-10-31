package com.example.backend.service;


import com.example.backend.converter.DTOConverter;
import com.example.backend.dto.PostResponseDTO;
import com.example.backend.model.Post;
import com.example.backend.model.QPost;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.MemberRepository;
import com.example.backend.repository.PostRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    private final JPAQueryFactory query;

//    public Post createPost(PostRequestDTO postRequestDTO) {
//
//        // 멤버 조회
//        Member member = memberRepository.findById(postRequestDTO.getMemberId())
//                .orElseThrow(() -> new ResourceNotFoundException("멤버를 찾을 수 없습니다."));
//
//        // 카테고리 조회
//        Category category = categoryRepository.findById(postRequestDTO.getCategoryId())
//                .orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다."));
//
//        // Post 객체 생성
//        Post post = new Post();
//        post.setMember(member);
//        post.setCategory(category);
//        post.setTitle(postRequestDTO.getTitle());
//        post.setDescription(postRequestDTO.getDescription());
//        post.setSellingArea(postRequestDTO.getSellingArea());
//
//
//    }


    // 전체 게시글 조회 // 최근 등록 순
    public List<PostResponseDTO> getAllPosts() {
        QPost qPost = QPost.post;

        // QueryDSL로 전체 게시글 조회
        List<Post> posts = query.selectFrom(qPost)
                .orderBy(qPost.createdAt.desc()) // 최신순 정렬
                .fetch();

        // Post 엔티티 리스트를 DTO 리스트로 변환
        List<PostResponseDTO> dtoList = DTOConverter.toDtoList(posts, post -> new PostResponseDTO(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getPostStatus(),
                post.getSellingArea(),
                post.getPrice(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCreatedAt(),
                post.getCategory().getName(),
                post.getMember().getNickname()
        ));

        // 변환된 DTO 리스트 반환
        return dtoList;
    }

    // 글 수정
    public boolean editPost(Long postId, Post updatedPost) {
        Optional<Post> existingPost = postRepository.findById(postId);
        if (existingPost.isPresent()) {
            Post post = existingPost.get();
            post.setTitle(updatedPost.getTitle());
            post.setDescription(updatedPost.getDescription());
            postRepository.save(post);
            return true;
        }
        return false;
    }

    // 상품 상세 조회
    public Optional<Post> showDetail(Long postId) {
        return postRepository.findById(postId);
    }

    // query
    // 검색(카테고리 기반)
    public List<Post> searchByCategory(String category) {
        return postRepository.findByCategory(category);
    }
}

