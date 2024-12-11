package com.example.backend.controller;

import com.example.backend.dto.auth.*;
import com.example.backend.service.AuthService;
import com.example.backend.util.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "인증", description = "인증 API")
public class AuthController {
    private final AuthService authService;
    private final TokenProvider tokenProvider;

    @Operation(summary = "비밀번호 재설정 전 인증", description = "비밀번호 재설정 전에 사용자를 인증합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameters({
            @Parameter(name = "loginId", description = "로그인 아이디"),
            @Parameter(name = "email", description = "이메일"),

    })
    @PostMapping("/check")
    public ResponseEntity<String> checkPassword(@RequestBody CheckAuthDTO checkAuth) {
        if(authService.checkAuth(checkAuth)){
            return ResponseEntity.ok("true");
        }else {
            return ResponseEntity.ok("false");
        }
    }

    @Operation(summary = "비밀번호 재설정", description = "사용자의 비밀번호를 재설정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameters({
            @Parameter(name = "loginId", description = "로그인 아이디"),
            @Parameter(name = "newPassword", description = "새로운 비밀번호"),

    })
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequestDTO passwordResetRequest) {
        authService.resetPassword(passwordResetRequest);
        return ResponseEntity.ok("비밀번호 재설정이 완료되었습니다.");
    }


    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameters({
            @Parameter(name = "loginId", description = "아이디"),
            @Parameter(name = "password", description = "비밀번호"),
            @Parameter(name = "name", description = "이름"),
            @Parameter(name = "identityNumber", description = "주민등록번호"),
            @Parameter(name = "phoneNumber", description = "전화번호"),
            @Parameter(name = "email", description = "이메일"),

    })
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDTO signupRequest) {
        authService.signup(signupRequest);
        log.info(signupRequest.toString());
        return ResponseEntity.ok("회원가입 성공");
    }

    @Operation(summary = "로그인", description = "사용자를 인증하고 로그인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameters({
            @Parameter(name = "loginId", description = "아이디"),
            @Parameter(name = "password", description = "비밀번호"),

    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        authService.login(loginRequest, response);
        return ResponseEntity.ok("로그인 성공");
    }


    @Operation(summary = "로그아웃", description = "사용자를 로그아웃합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameters({
            @Parameter(name = "loginId", description = "아이디"),
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequestDTO logoutRequest, HttpServletRequest request, HttpServletResponse response) {
        authService.logout(logoutRequest, request, response);
        return ResponseEntity.ok("로그아웃 성공");
    }

    @Operation(summary = "회원 탈퇴", description = "사용자 계정을 비활성화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameters({
            @Parameter(name = "loginId", description = "아이디"),
    })
    @PostMapping("/inactive")
    public ResponseEntity<String> inActiveMember(@RequestBody ActivityMemberRequestDTO activityMemberRequest) {
        authService.inActiveMember(activityMemberRequest);
        return ResponseEntity.ok("회원 탈퇴 성공");
    }

    @Operation(summary = "회원 활성화", description = "사용자 계정을 활성화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 활성화 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameters({
            @Parameter(name = "loginId", description = "아이디"),
    })
    @PostMapping("/active")
    public ResponseEntity<?> activateMember(@RequestBody ActivityMemberRequestDTO activityMemberRequest) {
        authService.activeMember(activityMemberRequest);
        return ResponseEntity.ok("회원 활성화 성공");
    }


    @Operation(summary = "아이디 중복 확인", description = "사용자 아이디의 중복 여부를 확인합니다.(true - 중복)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아이디 중복 여부 확인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameters({
            @Parameter(name = "loginId", description = "아이디"),
    })
    @PostMapping("/duplication/loginId")
    public ResponseEntity<Boolean> checkLoginID(@RequestBody CheckIdRequestDTO checkIdRequest) {
        boolean isDuplicate = authService.checkLoginID(checkIdRequest);
        return ResponseEntity.ok(isDuplicate);
    }


    @Operation(summary = "전화번호 중복 확인", description = "사용자 전화번호의 중복 여부를 확인합니다.(true - 중복)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전화번호 중복 여부 확인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameters({
            @Parameter(name = "phoneNumber", description = "전화번호"),
    })
    @PostMapping("/duplication/phone")
    public ResponseEntity<Boolean> checkPhone(@RequestBody CheckPhoneNumberRequestDTO checkPhoneRequest) {
        boolean isDuplicate = authService.checkPhoneNumber(checkPhoneRequest);
        return ResponseEntity.ok(isDuplicate);
    }


    @Operation(summary = "이메일 중복 확인", description = "사용자 이메일의 중복 여부를 확인합니다.(true - 중복)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 중복 여부 확인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameters({
            @Parameter(name = "email", description = "이메일"),
    })
    @PostMapping("/duplication/email")
    public ResponseEntity<Boolean> checkEmail(@RequestBody CheckEmailRequestDTO checkEmailRequest) {
        boolean isDuplicate = authService.checkEmail(checkEmailRequest);
        return ResponseEntity.ok(isDuplicate);
    }


    // 로그인 상태 확인
    // TODO
    //  -> 삭제 예정
    // 로그인 상태 확인
    @GetMapping("/check-login")
    public ResponseEntity<Map<String, Object>> checkLogin(HttpServletRequest request, HttpServletResponse response) {
        log.info("로그인 상태 확인 요청");

        String accessToken = tokenProvider.resolveAccessToken(request);
        Map<String, Object> responseMap = new HashMap<>();

        if (accessToken != null) {
            try {
                // accessToken 유효성 검증
                if (tokenProvider.validateToken(accessToken)) {
                    // 토큰이 유효한 경우 남은 유효 시간 확인
                    Date expiration = tokenProvider.getExpirationDate(accessToken);
                    long remainingTime = expiration.getTime() - new Date().getTime();

                    responseMap.put("status", "로그인 상태 유효");
                    responseMap.put("remainingTime", remainingTime);
                } else {
                    throw new ExpiredJwtException(null, null, "Token has expired");
                }
            } catch (ExpiredJwtException e) {
                log.info("Access token이 만료되었습니다. Refresh Token으로 갱신을 시도합니다...");

                String loginId = e.getClaims().getSubject();
                Long memberId = e.getClaims().get("memberId", Long.class);
                String refreshToken = tokenProvider.getRefreshTokenFromRedis(loginId);

                // Refresh Token 유효성 검증 후 새 Access Token 발급
                if (refreshToken == null) {
                    log.warn("Refresh Token이 없습니다. loginId: {}", loginId);
                    responseMap.put("status", "로그인 상태 만료");
                    responseMap.put("remainingTime", 0);
                } else if (!tokenProvider.validateToken(refreshToken)) {
                    log.warn("유효하지 않은 Refresh Token입니다. loginId: {}", loginId);
                    responseMap.put("status", "로그인 상태 만료");
                    responseMap.put("remainingTime", 0);
                } else {
                    String newAccessToken = tokenProvider.createAccessToken(loginId, memberId);
                    tokenProvider.setAccessTokenCookie(newAccessToken, response);

                    responseMap.put("status", "새로운 토큰 발급");
                    responseMap.put("newAccessToken", newAccessToken);
                    log.info("새로운 Access Token이 발급되었습니다. loginId: {}", loginId);
                }
            }
        } else {
            responseMap.put("status", "로그인 상태 만료");
            responseMap.put("remainingTime", 0);
        }

        return ResponseEntity.ok(responseMap);
    }


}
