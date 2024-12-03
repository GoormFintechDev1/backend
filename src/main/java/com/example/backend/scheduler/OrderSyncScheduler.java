package com.example.backend.scheduler;

import com.example.backend.service.POS.OrderSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSyncScheduler {

    private final OrderSyncService orderSyncService;

    @Scheduled(fixedRate = 30000)
    public void syncOrders() {
        orderSyncService.syncOrders();
    }
}
