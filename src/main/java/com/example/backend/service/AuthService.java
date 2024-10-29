package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.exception.base_exceptions.BadRequestException;
import com.example.backend.exception.base_exceptions.ResourceNotFoundException;
import com.example.backend.model.Member;
import com.example.backend.model.enumSet.MemberActiveEnum;
import com.example.backend.repository.MemberRepository;
import com.example.backend.util.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    // 회원가입
    public ResponseEntity<String> signup(SignupRequestDTO signupRequest) {
        log.info("회원가입 요청 수신: {}", signupRequest.getAccount());

        // 1. 중복 검사
        if (memberRepository.findByAccount(signupRequest.getAccount()).isPresent()) {
            log.warn("회원가입 실패: 중복된 아이디 {}", signupRequest.getAccount());
            throw new BadRequestException("해당 아이디는 이미 사용 중입니다");
        }
        if (memberRepository.findByNickname(signupRequest.getNickname()).isPresent()) {
            log.warn("회원가입 실패: 중복된 닉네임 {}", signupRequest.getNickname());
            throw new BadRequestException("해당 닉네임은 이미 사용 중입니다");
        }
        if (memberRepository.findByPhoneNumber(signupRequest.getPhoneNumber()).isPresent()) {
            log.warn("회원가입 실패: 중복된 휴대번호 {}", signupRequest.getPhoneNumber());
            throw new BadRequestException("해당 휴대번호는 이미 사용 중입니다");
        }

        // 2. 새로운 Member 객체 생성 및 정보 설정
        Member member = Member.builder()
                .account(signupRequest.getAccount())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .name(signupRequest.getName())
                .nickname(signupRequest.getNickname())
                .phoneNumber(signupRequest.getPhoneNumber())
                .address(signupRequest.getAddress())
                .build();

        // 3. DB에 저장
        memberRepository.save(member);
        log.info("회원가입 성공: {}", signupRequest.getAccount());
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인
    public String login(LoginRequestDTO loginRequest, HttpServletResponse response) {
        log.info("로그인 요청 수신: {}", loginRequest.getAccount());

        // 1. 계정 조회 및 예외 처리
        Member member = memberRepository.findByAccount(loginRequest.getAccount())
                .orElseThrow(() -> {
                    log.warn("로그인 실패: 잘못된 계정 {}", loginRequest.getAccount());
                    return new ResourceNotFoundException("존재하지 않는 계정입니다");
                });

        // 비활성화 상태라면 로그인 막기
        if (member.getActivity() == MemberActiveEnum.INACTIVE) {
            throw new BadRequestException("계정이 비활성화되었습니다. 활성화를 원하면 확인 버튼을 누르세요.");
        }

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            log.warn("로그인 실패: 잘못된 비밀번호");
            throw new BadRequestException("잘못된 비밀번호");
        }

        // 3. 액세스 토큰, 리프레시 토큰 생성
        String accessToken = tokenProvider.createAccessToken(member.getAccount());
        String refreshToken = tokenProvider.createRefreshToken(member.getAccount());
        log.info("토큰 생성 완료 - accessToken 및 refreshToken 생성");

        // 4. 리프레시 토큰 Redis에 저장
        tokenService.saveRefreshToken(member.getAccount(), refreshToken);
        log.info("Redis에 리프레시 토큰 저장 완료");

        // 5. 액세스 토큰을 쿠키에 저장
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(15 * 60);
        response.addCookie(accessTokenCookie);
        log.info("accessToken 쿠키 설정 완료: " + accessToken);

        return refreshToken;
    }

    // 로그아웃
    public void logout(LogoutRequestDTO logoutRequest, HttpServletResponse response) {
        log.info("로그아웃 요청 수신: {}", logoutRequest);

        // 1. 레디스에서 리프레시 토큰 삭제
        tokenService.deleteRefreshToken(logoutRequest.getAccount());
        log.info("Redis에서 리프레시 토큰 삭제 완료");

        // 2. 액세스 토큰 쿠키 삭제
        Cookie accessTokenCookie = new Cookie("accessToken", null); // 쿠키의 값을 null로 설정
        accessTokenCookie.setMaxAge(0); // 쿠키 만료 시간 0으로 설정
        accessTokenCookie.setPath("/"); // 해당 쿠키의 경로 설정
        accessTokenCookie.setHttpOnly(true); // HTTP 전송만 허용 (선택)
        response.addCookie(accessTokenCookie);

        log.info("accessToken 쿠키 만료 설정 완료");
        log.info("로그아웃 성공: {}", logoutRequest);
    }

    // 회원 탈퇴 (active -> inactive)
    public void inActiveMember(ActivityMemberRequestDTO activityMemberRequest) {
        Member member = memberRepository.findByAccount(activityMemberRequest.getAccount())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 회원입니다."));

        member.setActivity(MemberActiveEnum.INACTIVE);
        memberRepository.save(member); // 변경된 상태 저장

        log.info("회원 탈퇴 성공: {}", activityMemberRequest);
    }

    // 회원 활성화 (inactive -> active)
    public void activeMember(ActivityMemberRequestDTO activityMemberRequest) {
        Member member = memberRepository.findByAccount(activityMemberRequest.getAccount())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 회원입니다."));

        member.setActivity(MemberActiveEnum.ACTIVE);
        memberRepository.save(member); // 변경된 상태 저장

        log.info("회원 활성화 성공: {}", activityMemberRequest);
    }

    // 아이디 중복 확인 (true - 중복, false - 중복 아님)
    public boolean checkAccount(CheckIdRequestDTO checkIdRequest) {
        return memberRepository.findByAccount(checkIdRequest.getAccount()).isPresent();
    }

    // 닉네임 중복 확인 (true - 중복, false - 중복 아님)
    public boolean checkNickname(CheckNicknameRequestDTO checkNicknameRequest) {
        return memberRepository.findByNickname(checkNicknameRequest.getNickname()).isPresent();
    }

    // 폰 번호 중복 확인 (true - 중복, false - 중복 아님)
    public boolean checkPhoneNumber(CheckPhoneRequestDTO checkPhoneRequest) {
        return memberRepository.findByPhoneNumber(checkPhoneRequest.getPhone()).isPresent();
    }
}
