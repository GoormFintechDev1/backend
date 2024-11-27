package com.example.backend.service;

import com.example.backend.dto.pos.PosSalesRequestDTO;
import com.example.backend.model.Pos;
import com.example.backend.model.PosSales;
import com.example.backend.model.enumSet.PaymentTypeEnum;
import com.example.backend.repository.PosSalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PosSalesService {

    private final PosSalesRepository posSalesRepository;

    // 단일 저장 메서드
    public void saveSale(PosSalesRequestDTO request) {
        PosSales posSales = PosSales.builder()
                .pos(getDummyPos()) // 임시 POS 설정
                .saleDate(request.getSaleTime())
                .saleTime(request.getSaleTime())
                .paymentType(PaymentTypeEnum.CARD)
                .totalAmount(request.getTotalAmount())
                .vatAmount(calculateVAT(request.getTotalAmount()))
                .cardCompany("POS_CARD")
                .approvalNumber("APPROVED123")
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .build();

        posSalesRepository.save(posSales);
    }

    // 다중 저장 메서드
    @Transactional
    public void saveSales(List<PosSalesRequestDTO> requests) {
        requests.forEach(this::saveSale); // 요청 리스트 순회하며 개별 저장
    }

    // 부가세 계산 로직
    private BigDecimal calculateVAT(BigDecimal totalAmount) {
        // 부가세 계산 (예: 10%)
        return totalAmount.multiply(new BigDecimal("0.1"));
    }

    // 임시 POS 객체 생성
    private Pos getDummyPos() {
        return Pos.builder()
                .posId(1L) // 테스트용 POS ID
                .build();
    }
}
