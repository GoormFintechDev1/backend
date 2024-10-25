package com.example.backend.service;

import com.example.backend.dto.SignupRequestDTO;
import com.example.backend.model.Member;
import com.example.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    // 회원가입
    public Long saveMember(SignupRequestDTO dto) {
        // 비밀번호 암호화
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return memberRepository.save(
                        Member
                                .builder()
                                .account(dto.getAccount())
                                .password(encoder.encode(dto.getPassword()))
                                .name(dto.getName())
                                .nickname(dto.getNickname())
                                .phoneNumber(dto.getPhoneNumber())
                                .address(dto.getAddress())
                                .build()
                )
                .getId();
    }




}
