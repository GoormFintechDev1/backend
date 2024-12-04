package com.example.backend.service.POS;

import com.example.backend.dto.pos.OrderResponseDTO;
import com.example.backend.dto.pos.PosRequestDTO;
import com.example.backend.model.BUSINESS.BusinessRegistration;
import com.example.backend.model.Member;
import com.example.backend.model.POS.Pos;
import com.example.backend.repository.BusinessRegistrationRepository;
import com.example.backend.repository.MemberRepository;
import com.example.backend.repository.PosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PosOrderService {

    private final PosRepository posRepository;
    private final BusinessRegistrationRepository businessRegistrationRepository;

    @Qualifier("webClient8083")
    private final WebClient webClient;
    private final MemberRepository memberRepository;


    @Value("${pos.api.url.orders}")
    private String posOrdersUrl;

    public List<OrderResponseDTO> fetchOrdersFromPos() {
        return webClient.get()
                .uri(posOrdersUrl + "/all")
                .retrieve()
                .bodyToFlux(OrderResponseDTO.class)
                .collectList()
                .block();
    }

    public void savePosData(Long memberId, String brNum) {
        // Step 1: Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // Step 2: Member를 통해 BusinessRegistration 확인
        BusinessRegistration businessRegistration = member.getBusinessRegistration();
        if (businessRegistration == null) {
            throw new IllegalArgumentException("BusinessRegistration not found for this member");
        }

        // Step 3: WebClient로 POS ID 가져오기
        Long posId = fetchPosIdFromPosService(brNum);

        // Step 4: POS 데이터 생성 및 저장
        Pos pos = Pos.builder()
                .posId(posId) // 받아온 posId 그대로 저장
                .brNum(brNum)
                .build();
        posRepository.save(pos);

        // Step 5: BusinessRegistration에 POS 설정
        businessRegistration.setPos(pos);
        businessRegistrationRepository.save(businessRegistration);
    }

    private Long fetchPosIdFromPosService(String brNum) {
        try {
            // 요청 DTO 생성
            PosRequestDTO requestDTO = new PosRequestDTO(null, brNum); // posId는 null
            return webClient.post()
                    .uri("http://localhost:8083/api/pos/get-pos-id")
                    .bodyValue(requestDTO)
                    .retrieve()
                    .bodyToMono(Long.class)
                    .block();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to fetch POS ID from POS service", e);
        }
    }

}
