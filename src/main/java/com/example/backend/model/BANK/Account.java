package com.example.backend.model.BANK;

import com.example.backend.model.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseTime {

    // 계좌식별 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    // 계좌번호
    @Column(name = "account_num")
    private String accountNumber;

    // 은행 이름
    @Column(name = "bank_name")
    private String bankName;

    // 계좌 잔액
    @Column(name = "balance", precision = 15, scale = 0)
    private BigDecimal balance;

    // 외부API에서 받아온 사업자 번호 (초기 인증용)
    @Column(name = "br_num", nullable = false, unique = true)
    private String brNum;
}
