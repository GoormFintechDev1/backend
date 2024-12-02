package com.example.backend.dto.pos;

import com.example.backend.model.enumSet.OrderStatus;
import com.example.backend.model.enumSet.PaymentStatus;
import com.example.backend.model.enumSet.PaymentType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PosSalesRequestDTO {
    private String productName;           // 상품명
    private int quantity;                 // 수량
    private BigDecimal totalPrice;        // 총 금액
    private LocalDateTime orderTime;      // 주문 시간
    private OrderStatus orderStatus;      // 주문 상태
    private PaymentType paymentType;      // 결제 타입
    private PaymentStatus paymentStatus;  // 결제 상태
}
