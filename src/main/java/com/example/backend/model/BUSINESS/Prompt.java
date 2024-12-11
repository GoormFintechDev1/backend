package com.example.backend.model.BUSINESS;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prompt")
public class Prompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promptId;

    @Column(name = "month")
    private YearMonth month;

    @Column(name = "type") // 트렌드 & 시장 이슈
    private String type;

    @Column(name = "contents")
    private String contents;
}
