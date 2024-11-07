package com.example.backend.model;

import com.example.backend.model.enumSet.PaymentTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "pos_sale")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PosSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "pos_id", nullable = false)
    private Long posId;

    @Column(name = "sale_date", nullable = false)
    private LocalDateTime saleDate;

    @Column(name = "sale_time", nullable = false)
    private LocalDateTime saleTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentTypeEnum paymentType;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "vat_amount", precision = 15, scale = 2)
    private BigDecimal vatAmount;

    @Column(name = "card_company", length = 50)
    private String cardCompany;

    @Column(name = "approval_number", length = 20)
    private String approvalNumber;
}