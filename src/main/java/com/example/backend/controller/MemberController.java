package com.example.backend.controller;

import com.example.backend.dto.member.myPageDTO;
import com.example.backend.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "멤버", description = "멤버 API")
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "마이페이지 정보 조회", description = "사용자의 마이페이지 정보를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마이페이지 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })

    @GetMapping("/info")
    public ResponseEntity<myPageDTO> info( @AuthenticationPrincipal Long memberId){
        myPageDTO myInfo = memberService.showMemberInfo(memberId);
        return ResponseEntity.ok(myInfo);
    }


}
