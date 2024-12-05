package com.example.backend.controller.POS;

import com.example.backend.service.POS.OrderSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pos-sales")
@RequiredArgsConstructor
public class PosSalesController {

    private final OrderSyncService orderSyncService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncSales() {
        try {
            orderSyncService.syncOrders();
            return ResponseEntity.ok("POS Sales synced successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to sync POS Sales: " + e.getMessage());
        }
    }


//    @PostMapping("/save")
//    public ResponseEntity<String> saveSales(@RequestBody List<PosSalesRequestDTO> requests) {
//        try {
//            // 서비스 호출
//            posSalesService.saveSales(requests);
//            return ResponseEntity.ok("Sales data saved successfully.");
//        } catch (Exception e) {
//            // 에러 응답 처리
//            return ResponseEntity.status(500).body("Error saving sales data: " + e.getMessage());
//        }
//    }
}
