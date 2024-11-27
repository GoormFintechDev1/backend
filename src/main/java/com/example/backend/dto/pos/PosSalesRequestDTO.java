package com.example.backend.dto.pos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PosSalesRequestDTO {
    private String productName;
    private int quantity;
    private BigDecimal totalAmount;
    private LocalDateTime saleTime;
}
