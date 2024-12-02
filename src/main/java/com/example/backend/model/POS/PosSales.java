package com.example.backend.model.POS;

import com.example.backend.model.enumSet.OrderStatus;
import com.example.backend.model.enumSet.PaymentStatus;
import com.example.backend.model.enumSet.PaymentTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "pos_sales")
public class PosSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pos_sales_id")
    private Long posSalesId;

    @ManyToOne
    @JoinColumn(name = "pos_id", nullable = false)
    private Pos pos;

    // 주문 시간
    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    // 합산 가격
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    // totalPrice의 10%
    @Column(name = "vat_amount", nullable = false)
    private BigDecimal vatAmount;

    // 물건 이름
    @Column(name = "product_name", nullable = false)
    private String productName;

    // 수량
    @Column(name = "quantity", nullable = false)
    private int quantity;

    // 주문 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    // 결제 유형 ( CARD / CASH )
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentTypeEnum paymentType;


    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus; // 결제 상태


}
