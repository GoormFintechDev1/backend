package com.example.backend.controller.BUSINESS;

import com.example.backend.dto.auth.CheckBusinessDTO;
import com.example.backend.dto.pos.PosRequestDTO;
import com.example.backend.service.BUSINESS.BusinessService;
import com.example.backend.service.POS.PosOrderService;
import com.example.backend.service.POS.PosService;
import com.example.backend.util.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "사업자", description = "사업자 API")
public class BusinessController {

    private final BusinessService businessService;
    private final TokenProvider tokenProvider;
    private final PosOrderService posOrderService;


    @Operation(summary = "사업자 인증", description = "사용자의 사업자 정보를 인증합니다.( + pos와 bank 정보를 가져옵니다.)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사업자 인증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameters({
            @Parameter(name = "brNum", description = "사업자 번호"),
            @Parameter(name = "address", description = "사업자 주소"),
    })
    @PostMapping("/br-connect")
    public ResponseEntity<String> authentiateBusiness(HttpServletRequest request, @RequestBody CheckBusinessDTO checkBusinessRequest) {
        String token = tokenProvider.resolveAccessToken(request);
        Long memberId = tokenProvider.getMemberIdFromToken(token);

        businessService.verifyBusiness(memberId, checkBusinessRequest);
        return ResponseEntity.ok("사업자 인증 성공");
    }


}


