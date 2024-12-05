package com.example.backend.controller;

import com.example.backend.dto.member.myPageDTO;
import com.example.backend.service.MemberService;
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
public class MemberController {
    private final MemberService memberService;
    @GetMapping("/info")
    public ResponseEntity<myPageDTO> info( @AuthenticationPrincipal Long memberId){
        myPageDTO myInfo = memberService.showMemberInfo(memberId);
        return ResponseEntity.ok(myInfo);
    }


}
