package com.example.backend.controller.POS;

import com.example.backend.dto.pos.PosSalesRequestDTO;
import com.example.backend.service.POS.PosSalesService;
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

    private final PosSalesService posSalesService;

    @PostMapping("/save")
    public ResponseEntity<String> saveSales(@RequestBody List<PosSalesRequestDTO> requests) {
        try {
            // 서비스 호출
            posSalesService.saveSales(requests);
            return ResponseEntity.ok("Sales data saved successfully.");
        } catch (Exception e) {
            // 에러 응답 처리
            return ResponseEntity.status(500).body("Error saving sales data: " + e.getMessage());
        }
    }
}
