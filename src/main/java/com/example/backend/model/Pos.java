package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "pos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pos_id")
    private Long posId;

    // 외부API에서 받아온 사업자 번호 (초기 인증용)
    @Column(name = "br_num", nullable = false, unique = true)
    private String brNum;

}
