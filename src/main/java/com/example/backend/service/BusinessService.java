package com.example.backend.service;

import com.example.backend.dto.CheckBusinessDTO;
import com.example.backend.exception.base_exceptions.BadRequestException;
import com.example.backend.model.BusinessRegistration;
import com.example.backend.model.Member;
import com.example.backend.repository.BusinessRepository;
import com.example.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final MemberRepository memberRepository;

    public ResponseEntity<String> checkBusiness(Long memberId, CheckBusinessDTO checkBusinessRequest) {
        log.info("사업자 등록 번호 확인 로직 진입 {}", checkBusinessRequest);

        // 1. 입력 값과 사업자 등록 번호가 일치하는 것이 있는지 확인
        BusinessRegistration business = businessRepository.findByBrNum(checkBusinessRequest.getBrNum())
                .orElseThrow(() -> new BadRequestException("존재하지 않는 사업자 등록 번호입니다."));

        // 2. 이미 다른 member와 연결된 지 확인
        if (business.getMember() != null) {
            throw new BadRequestException("이미 다른 회원과 연결된 사업자입니다.");
        }

        // 3. member의 name과 사업자의 representative name이 같은지 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 회원입니다."));
        String memberName = member.getName();
        String businessName = business.getRepresentativeName();

        if (!memberName.equals(businessName)) {
            throw new BadRequestException("회원의 이름과 사업자의 대표자 이름이 일치하지 않습니다.");
        }

        // 4. 비어있던 사업자 테이블에 멤버 아이디가 연결됨
        business.setMember(member);
        businessRepository.save(business); //memberId를 채우고 저장
        return ResponseEntity.ok("사업자 인증 성공");
    }
}