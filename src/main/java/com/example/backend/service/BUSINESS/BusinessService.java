package com.example.backend.service.BUSINESS;

import com.example.backend.dto.auth.BusinessRegistrationDTO;
import com.example.backend.dto.auth.CheckBusinessDTO;
import com.example.backend.exception.base_exceptions.BadRequestException;
import com.example.backend.model.BUSINESS.BusinessRegistration;
import com.example.backend.model.BUSINESS.QBusinessRegistration;
import com.example.backend.model.Member;
import com.example.backend.repository.BusinessRepository;
import com.example.backend.repository.MemberRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final MemberRepository memberRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final WebClient webClient;
//
//    // 로그인한 유저의 businessID를 가져오는 로직
//    public BusinessRegistration getBusinessIdByMemberID(Long memberID) {
//        QBusinessRegistration qBusinessRegistration = QBusinessRegistration.businessRegistration;
//
//        BusinessRegistration business = jpaQueryFactory
//                .select(qBusinessRegistration)
//                .from(qBusinessRegistration)
//                .where(qBusinessRegistration.member.id.eq(memberID))
//                .fetchOne();
//
//        if (business == null) {
//            throw new BadRequestException("해당 회원과 연결된 사업자가 없습니다");
//        }
//        return business;
//    }


    // 사업자 인증하는 로직
    public ResponseEntity<String> checkBusiness(Long memberId, CheckBusinessDTO checkBusinessRequest) {
        log.info("!!!!!!!사업자 등록 번호 확인 로직 진입 {}", memberId);

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
        business.setMember(member);
        business.setBrNum(externalBusiness.getBrNum());
        business.setAddress(externalBusiness.getAddress());
        business.setBusinessType(externalBusiness.getBusinessType());
        business.setBusinessItem(externalBusiness.getBusinessItem());
        business.setCompanyName(externalBusiness.getCompanyName());
        business.setRepresentativeName(externalBusiness.getRepresentativeName());


        businessRepository.save(business); // 연결 완료
        log.info("사업자 인증 성공 for Member ID: {}", memberId);
    }

}

