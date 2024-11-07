package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "pos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pos_id")
    private Long posId;

    @ManyToOne
    @JoinColumn(name = "business_id", nullable = false)
    private BusinessRegistration businessRegistration;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "income", precision = 15, scale = 0)
    private BigDecimal income;

    @OneToMany(mappedBy = "pos", cascade = CascadeType.ALL)
    private List<POSSales> sales;

}
