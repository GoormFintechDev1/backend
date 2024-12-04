package com.example.backend.controller.BUSINESS;

import com.example.backend.dto.auth.CheckBusinessDTO;
import com.example.backend.dto.pos.PosRequestDTO;
import com.example.backend.service.BUSINESS.BusinessService;
import com.example.backend.service.POS.PosOrderService;
import com.example.backend.service.POS.PosService;
import com.example.backend.util.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
@Slf4j
public class BusinessController {

    private final BusinessService businessService;
    private final TokenProvider tokenProvider;
    private final PosOrderService posOrderService;

    // 사업자 인증
//    @PostMapping("/check")
//    public ResponseEntity<String> checkBusiness(HttpServletRequest request, @RequestBody CheckBusinessDTO checkBusinessRequest) {
//        // 요청에서 토큰을 가져와 memberId를 추출
//        String token = tokenProvider.resolveAccessToken(request);
//        Long memberId = tokenProvider.getMemberIdFromToken(token);
//
//        // BusinessService 호출
//        businessService.checkBusiness(memberId, checkBusinessRequest);
//        return ResponseEntity.ok("사업자 인증 성공");
//    }


    // <<<NEW>>> 사업자 인증 API
    @PostMapping("/br-connect")
    public ResponseEntity<String> authentiateBusiness(HttpServletRequest request, @RequestBody CheckBusinessDTO checkBusinessRequest) {
        String token = tokenProvider.resolveAccessToken(request);
        Long memberId = tokenProvider.getMemberIdFromToken(token);

        businessService.verifyBusiness(memberId, checkBusinessRequest);
        return ResponseEntity.ok("사업자 인증 성공");
    }

    @PostMapping("/pos-connect")
    public ResponseEntity<String> authenticatePos(HttpServletRequest request, @RequestBody PosRequestDTO posRequestDTO) {

        // 토큰에서 memberId 추출
        String token = tokenProvider.resolveAccessToken(request);
        Long memberId = tokenProvider.getMemberIdFromToken(token);

        // POS ID 저장 로직 호출
        posOrderService.savePosData(memberId, posRequestDTO.getBrNum());


        return ResponseEntity.ok("포스 인증 성공");
    }

}


