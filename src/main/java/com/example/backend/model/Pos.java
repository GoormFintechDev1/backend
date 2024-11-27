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

    @OneToOne // 한 사람은 포스를 한 개만 갖도록
    @JoinColumn(name = "business_id", nullable = false, unique = true)
    private BusinessRegistration businessRegistration;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "income", precision = 15, scale = 0)
    private BigDecimal income;

    @OneToMany(mappedBy = "pos", cascade = CascadeType.ALL)
    private List<PosSales> sales;

}
