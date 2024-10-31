package com.example.backend.repository;
import com.example.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public  interface PostRepository extends JpaRepository<Post, Long> {

    // 검색(카테고리 기반)

}
