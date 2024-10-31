package com.example.backend.repository;

import com.example.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public  interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByCategory(String category);
}