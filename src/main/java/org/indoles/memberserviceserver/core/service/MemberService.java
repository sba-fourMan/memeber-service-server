package org.indoles.memberserviceserver.core.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.memberserviceserver.core.dto.SignUpRequestInfo;
import org.indoles.memberserviceserver.core.domain.Member;
import org.indoles.memberserviceserver.core.dto.SignInRequestInfo;
import org.indoles.memberserviceserver.core.dto.SignInInfo;
import org.indoles.memberserviceserver.core.infra.MemberCoreRepository;
import org.indoles.memberserviceserver.global.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.indoles.memberserviceserver.global.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberCoreRepository memberCoreRepository;

    @Transactional
    public void signUp(SignUpRequestInfo signUpRequestInfo) {
        try {
            if (memberCoreRepository.isExist(signUpRequestInfo.signUpId())) {
                throw new BadRequestException("이미 존재하는 아이디입니다. input=" + signUpRequestInfo.signUpId(), M000);
            }
            Member member = Member.createMemberWithRole(
                    signUpRequestInfo.signUpId(),
                    signUpRequestInfo.password(),
                    signUpRequestInfo.userRole()
            );

            memberCoreRepository.save(member);
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생", e);
            throw e;
        }
    }

    public SignInInfo signIn(SignInRequestInfo signInRequestInfo) {
        try {
            Member member = memberCoreRepository.findBySignInId(signInRequestInfo.signInId())
                    .orElseThrow(() -> new BadRequestException(
                            "아이디에 해당되는 사용자를 찾을 수 없습니다. signInId=" + signInRequestInfo.signInId(),
                            M002));

            if (!member.confirmPassword(signInRequestInfo.password())) {
                throw new BadRequestException("패스워드가 일치하지 않습니다.", M003);
            }

            return new SignInInfo(member.getId(), member.getRole());
        } catch (Exception e) {
            log.error("로그인 중 오류 발생", e);
            throw e;
        }
    }
}
