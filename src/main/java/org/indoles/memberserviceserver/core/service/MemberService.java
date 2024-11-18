package org.indoles.memberserviceserver.core.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.memberserviceserver.core.dto.request.SignUpRequest;
import org.indoles.memberserviceserver.core.domain.Member;
import org.indoles.memberserviceserver.core.dto.request.SignInRequest;
import org.indoles.memberserviceserver.core.dto.response.SignInfoRequest;
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
    public void signUp(SignUpRequest signUpRequest) {
        try {
            if (memberCoreRepository.isExist(signUpRequest.signUpId())) {
                throw new BadRequestException("이미 존재하는 아이디입니다. input=" + signUpRequest.signUpId(), M000);
            }
            Member member = Member.createMemberWithRole(
                    signUpRequest.signUpId(),
                    signUpRequest.password(),
                    signUpRequest.userRole()
            );

            memberCoreRepository.save(member);
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생", e);
            throw e;
        }
    }

    public SignInfoRequest signIn(SignInRequest signInRequest) {
        try {
            Member member = memberCoreRepository.findBySignInId(signInRequest.signInId())
                    .orElseThrow(() -> new BadRequestException(
                            "아이디에 해당되는 사용자를 찾을 수 없습니다. signInId=" + signInRequest.signInId(),
                            M002));

            if (!member.confirmPassword(signInRequest.password())) {
                throw new BadRequestException("패스워드가 일치하지 않습니다.", M003);
            }

            return new SignInfoRequest(member.getId(), member.getRole());
        } catch (Exception e) {
            log.error("로그인 중 오류 발생", e);
            throw e;
        }
    }
}
