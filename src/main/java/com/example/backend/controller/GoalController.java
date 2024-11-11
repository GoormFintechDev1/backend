package com.example.backend.controller;

import com.example.backend.dto.goals.GoalRequestDTO;
import com.example.backend.dto.goals.GoalResponseDTO;
import com.example.backend.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/goal")
public class GoalController {

    private final GoalService goalService;

    // 목표 설정
    @PostMapping("/set")
    public ResponseEntity<String> setGoal(@AuthenticationPrincipal Long memberId, @RequestBody GoalRequestDTO goalRequestDTO) {
        goalService.setGoal(memberId, goalRequestDTO);
        return ResponseEntity.ok("목표 설정 완료!");
    }

    // 목표 업데이트
    @PutMapping("/update")
    public ResponseEntity<String> updateGoal(@AuthenticationPrincipal Long memberId, @RequestBody GoalRequestDTO goalRequestDTO) {
        goalService.updateGoal(memberId, goalRequestDTO);
        return ResponseEntity.ok("목표 재설정 완료!");
    }
    // 목표 달성 여부 확인 및 업데이트
    @GetMapping("/check")
    public ResponseEntity<GoalResponseDTO> checkGoal(@AuthenticationPrincipal Long memberId, @RequestParam LocalDate goalMonth) {
        GoalResponseDTO responseDTO = goalService.goalCheck(memberId, goalMonth);
        return ResponseEntity.ok(responseDTO);
    }

}
