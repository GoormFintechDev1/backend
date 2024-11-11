package com.example.backend.service;

import com.example.backend.dto.member.myPageDTO;
import com.example.backend.model.QBusinessRegistration;
import com.example.backend.model.QMember;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final JPAQueryFactory queryFactory;

    public myPageDTO showMemberInfo(Long memberId) {
        QMember qMember = QMember.member;
        QBusinessRegistration qBusinessRegistration = QBusinessRegistration.businessRegistration;

        Tuple result = queryFactory
                .select(
                        qMember.name,
                        qMember.phoneNumber,
                        qMember.email,
                        qBusinessRegistration.brNum,
                        qBusinessRegistration.address,
                        qBusinessRegistration.businessStartDate,
                        qBusinessRegistration.companyName
                )
                .from(qMember)
                .leftJoin(qBusinessRegistration)
                .on(qMember.id.eq(qBusinessRegistration.member.id))
                .where(qMember.id.eq(memberId))
                .fetchOne();


        return new myPageDTO(
                    result.get(qMember.name),
                    result.get(qMember.phoneNumber),
                    result.get(qMember.email),
                    result.get(qBusinessRegistration.brNum),
                    result.get(qBusinessRegistration.address),
                    result.get(qBusinessRegistration.businessStartDate),
                    result.get(qBusinessRegistration.companyName)
        );
    }
}
