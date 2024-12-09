package com.example.backend.controller;

import com.example.backend.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
@Tag(name = "레디스", description = "레디스 API")
public class RedisTestController {

    @Autowired
    private RedisService redisService;


    @Operation(summary = "Redis 키-값 저장", description = "Redis에 키-값 쌍을 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "키-값 저장 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/set")
    public String setKeyValue(@RequestParam String key, @RequestParam String value) {
        redisService.saveValue(key, value);
        return "Saved key-value pair to Redis: " + key + " - " + value;
    }

    @Operation(summary = "Redis 값 조회", description = "Redis에서 특정 키의 값을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "값 조회 성공"),
            @ApiResponse(responseCode = "404", description = "키를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/get")
    public String getKeyValue(@RequestParam String key) {
        String value = redisService.getValue(key);
        return value != null ? "Retrieved from Redis: " + value : "Key not found";
    }

    @Operation(summary = "Redis 키 삭제", description = "Redis에서 특정 키를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "키 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "키를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/delete")
    public String deleteKeyValue(@RequestParam String key) {
        redisService.deleteValue(key);
        return "Deleted key from Redis: " + key;
    }
}
