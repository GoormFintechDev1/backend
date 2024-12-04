package com.example.backend.service.BUSINESS;

import com.example.backend.dto.auth.BusinessRegistrationDTO;
import com.example.backend.dto.auth.CheckBusinessDTO;
import com.example.backend.exception.base_exceptions.BadRequestException;
import com.example.backend.model.BUSINESS.BusinessRegistration;
import com.example.backend.model.BUSINESS.QBusinessRegistration;
import com.example.backend.model.Member;
import com.example.backend.model.QMember;
import com.example.backend.repository.BusinessRepository;
import com.example.backend.repository.MemberRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final MemberRepository memberRepository;
    private final JPAQueryFactory queryFactory;

    @Qualifier("webClient8084")
    private final WebClient webClient;

    // 로그인한 유저의 businessID를 가져오는 로직
    public BusinessRegistration getBusinessIdByMemberID(Long memberId){
        // BusinessRegistration 조회
        BusinessRegistration businessRegistration = queryFactory
                .selectFrom(QBusinessRegistration.businessRegistration)
                .join(QBusinessRegistration.businessRegistration)
                .on(QBusinessRegistration.businessRegistration.businessRegistrationId.eq(QMember.member.businessRegistration.businessRegistrationId)) // 연결
                .where(QMember.member.memberId.eq(memberId)) // memberId 조건
                .fetchOne();

        if (businessRegistration == null){
            throw new BadRequestException("해당 회원과 연결된 사업자가 없습니다");
        }
        return businessRegistration;
    }

    // 사업자 외부 API에서 인증하는 로직 (최신)
    public void verifyBusiness(Long memberId, CheckBusinessDTO checkBusinessRequest) {
        log.info("사업자 인증 진행 중 for Member ID: {}", memberId);

        // 1. 외부 API 호출하여 사업자 정보 확인
        BusinessRegistrationDTO externalBusiness = webClient.get()
                .uri("http://localhost:8084/business/{brNum}", checkBusinessRequest.getBrNum())
                .retrieve()
                .bodyToMono(BusinessRegistrationDTO.class)
                .block();

        if (externalBusiness == null) {
            throw new BadRequestException("유효하지 않은 사업자 등록번호입니다.");
        }

        // 2. 기존 로직: Member와 Business 정보 연계
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 회원입니다."));

        if (!externalBusiness.getRepresentativeName().equals(member.getName())) {
            throw new BadRequestException("회원의 이름과 사업자의 대표자 이름이 일치하지 않습니다.");
        }

        BusinessRegistration business = new BusinessRegistration();

        member.setBusinessRegistration(business);
        business.setBrNum(externalBusiness.getBrNum());
        business.setAddress(externalBusiness.getAddress());
        business.setBusinessType(externalBusiness.getBusinessType());
        business.setBusinessItem(externalBusiness.getBusinessItem());
        business.setCompanyName(externalBusiness.getCompanyName());
        business.setRepresentativeName(externalBusiness.getRepresentativeName());


        member.setBusinessRegistration(business);

        businessRepository.save(business);
        memberRepository.save(member);

        log.info("사업자 인증 성공 for Member ID: {}", memberId);
    }



}

