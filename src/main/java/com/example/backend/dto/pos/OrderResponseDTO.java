package com.example.backend.dto.pos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long orderId;
    private LocalDateTime orderDate;
    private int totalPrice;
    private String productName;
    private int quantity;
    private String orderStatus;
    private String paymentStatus;
    private Long posId;
}
