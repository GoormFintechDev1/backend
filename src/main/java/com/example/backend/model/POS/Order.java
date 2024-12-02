package com.example.backend.model.POS;


import com.example.backend.model.enumSet.OrderStatus;
import com.example.backend.model.enumSet.PaymentStatus;
import com.example.backend.model.enumSet.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

//    Doubly의 PosSales로 전송될 테이블.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "pos_id", nullable = false)
    private Pos posId;

    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    // totalPrice의 10%
    @Column(name = "vat_amount", nullable = false)
    private BigDecimal vatAmount;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus; // 주문 상태

    // 결제 유형 ( CARD / CASH )
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus; // 결제 상태




}
