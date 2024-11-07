package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseTime{

    // 계좌식별 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    // 사업자번호
    @ManyToOne
    @JoinColumn(name = "business_id")
    private BusinessRegistration business;

    // 계좌번호
    @Column(name = "account_num")
    private String accountNumber;

    // 은행 이름
    @Column(name = "bank_name")
    private String bankName;

    // 잔고
    @Column(name = "balance", precision = 15, scale = 0)
    private BigDecimal balance;
}
