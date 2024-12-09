package com.example.backend.controller.POS;

import com.example.backend.service.POS.OrderSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "포스 매출", description = "포스 매출 API")
public class PosSalesController {

    private final OrderSyncService orderSyncService;


    @Operation(summary = "POS 매출 데이터 동기화", description = "POS 시스템의 매출 데이터를 동기화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "POS 매출 데이터 동기화 성공"),
            @ApiResponse(responseCode = "500", description = "POS 매출 데이터 동기화 실패")
    })

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

}
