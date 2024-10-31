package com.example.backend.repository;
import com.example.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface PostRepository extends JpaRepository<Post, Long> {
//
//    public Post findByKeyword(String keyword);
//    public Post findAllByOrderByIdDesc();

    // 검색(카테고리 기반)

}
