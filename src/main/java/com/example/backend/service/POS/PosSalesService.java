//package com.example.backend.service.POS;
//
//import com.example.backend.dto.pos.PosSalesRequestDTO;
//import com.example.backend.model.POS.Pos;
//import com.example.backend.model.POS.PosSales;
//import com.example.backend.repository.PosSalesRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class PosSalesService {
//
//    private final PosSalesRepository posSalesRepository;
//    private final WebClient webClient;
//
//
//    // POS(8083)에서 데이터 가져오기
//    public List<PosSalesRequestDTO> fetchPosSales() {
//        String posUrl= "http://localhost:8083/api/orders";
//
//        return webClient.get()
//                .uri(posUrl)
//                .retrieve()
//                .bodyToFlux(PosSalesRequestDTO.class)
//                .collectList()
//                .block();
//    }
//
//
//
//    // 단일 저장 메서드
//    public PosSales saveSale(PosSalesRequestDTO request) {
//        PosSales posSales = PosSales.builder()
//                .posId(getDummyPos()) // 임시 POS 설정
//                .productName(request.getProductName())
//                .quantity(request.getQuantity())
//                .totalPrice(request.getTotalPrice())
//                .vatAmount(calculateVAT(request.getTotalPrice()))
//                .orderTime(request.getOrderTime())
//                .orderStatus(request.getOrderStatus())
//                .paymentType(request.getPaymentType())
//                .paymentStatus(request.getPaymentStatus())
//                .build();
//
//        return posSalesRepository.save(posSales); // 저장된 엔티티 반환
//
//    }
//
//    // 다중 저장 메서드
//    @Transactional
//    public void saveSales(List<PosSalesRequestDTO> requests) {
//        requests.forEach(this::saveSale); // 요청 리스트 순회하며 개별 저장
//    }
//
//    // 부가세 계산 로직
//    private BigDecimal calculateVAT(BigDecimal totalAmount) {
//        // 부가세 계산 (예: 10%)
//        return totalAmount.multiply(new BigDecimal("0.1"));
//    }
//
//    // 임시 POS 객체 생성
//    private Pos getDummyPos() {
//        return Pos.builder()
//                .posId(1L) // 테스트용 POS ID
//                .build();
//    }
//}