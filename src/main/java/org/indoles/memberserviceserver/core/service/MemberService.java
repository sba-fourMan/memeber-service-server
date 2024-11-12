package org.indoles.memberserviceserver.core.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.memberserviceserver.core.dto.request.SignUpRequestInfo;
import org.indoles.memberserviceserver.core.entity.MemberEntity;
import org.indoles.memberserviceserver.core.entity.exception.MemberException;
import org.indoles.memberserviceserver.core.domain.Member;
import org.indoles.memberserviceserver.core.domain.Point;
import org.indoles.memberserviceserver.core.dto.request.SignInRequestInfo;
import org.indoles.memberserviceserver.core.dto.response.SignInInfo;
import org.indoles.memberserviceserver.core.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void signUp(SignUpRequestInfo signUpRequestInfo) {
        try {
            if (memberRepository.existsBySignInId(signUpRequestInfo.signUpId())) {
                throw new MemberException(ALREADY_EXISTS);
            }
            Member member = Member.createMemberWithRole(
                    signUpRequestInfo.signUpId(),
                    signUpRequestInfo.password(),
                    signUpRequestInfo.userRole()
            );

            MemberEntity memberEntity = Member.toEntity(member);
            memberRepository.save(memberEntity);
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생", e);
            throw e;
        }
    }

    public SignInInfo signIn(SignInRequestInfo signInRequestInfo) {
        try {
            MemberEntity memberEntity = memberRepository.findBySignInId(signInRequestInfo.signInId())
                    .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

            // MemberEntity -> Member로 변환
            Member member = memberEntity.toDomain();

            if (!member.confirmPassword(signInRequestInfo.password())) {
                throw new MemberException(WRONG_PASSWORD);
            }

            return new SignInInfo(member.getId(), member.getRole());
        } catch (Exception e) {
            log.error("로그인 중 오류 발생", e);
            throw e;
        }
    }

    @Transactional
    public void chargePoint(SignInInfo memberInfo, long chargePoint) {
        try {
            Point points = new Point(chargePoint);

            MemberEntity memberEntity = memberRepository.findById(memberInfo.id())
                    .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

            Member member = memberEntity.toDomain();
            member.chargePoint(chargePoint);

            memberRepository.save(Member.toEntity(member));
        } catch (Exception e) {
            log.error("포인트 충전 중 오류 발생", e);
            throw e;
        }
    }
}
