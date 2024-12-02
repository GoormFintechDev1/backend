package com.example.backend.dto.pos;

import java.time.LocalDateTime;
import java.util.List;

public class ExternalOrderDTO {
    private Long orderId;
    private String customerName;
    private LocalDateTime orderDate;
    private List<ExternalOrderItemDTO> orderItems;
}
