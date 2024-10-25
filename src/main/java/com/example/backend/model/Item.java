package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor // 생성자 자동 생성
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;


    // 카테고리 Enum 정의
    public enum Category{

    }
    private String title;
    private String contents;
    private Item.Category category;
  //  private List<> attachments = new ArrayList<>();

    private String price;
    private int viewCount;
    private int likesCount;


}


//title, contents, image, price, likesCount, viewCount,
//createdAt, updateAt,