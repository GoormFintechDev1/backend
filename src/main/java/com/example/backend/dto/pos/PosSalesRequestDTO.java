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
    private Long posSalesId;
    private Long posId;
    private LocalDateTime orderTime;
    private BigDecimal totalPrice;
    private BigDecimal vatAmount;
    private String productName;
    private int quantity;
    private String orderStatus;
    private String paymentStatus;
}
