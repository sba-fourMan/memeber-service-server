package org.indoles.memberserviceserver.service;


import lombok.RequiredArgsConstructor;
import org.indoles.memberserviceserver.domain.Member;
import org.indoles.memberserviceserver.dto.request.SignInRequestInfo;
import org.indoles.memberserviceserver.dto.request.SignUpRequestInfo;
import org.indoles.memberserviceserver.dto.response.SignInResponseInfo;
import org.indoles.memberserviceserver.entity.MemberEntity;
import org.indoles.memberserviceserver.entity.exception.MemberException;
import org.indoles.memberserviceserver.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.indoles.memberserviceserver.entity.exception.MemberExceptionCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void signUp(SignUpRequestInfo signUpRequestInfo) {
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
    }

    public SignInResponseInfo signIn(SignInRequestInfo signInRequestInfo) {
        MemberEntity memberEntity = memberRepository.findBySignInId(signInRequestInfo.signInId())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        // MemberEntity -> Member로 변환
        Member member = memberEntity.toDomain();

        if (!member.confirmPassword(signInRequestInfo.password())) {
            throw new MemberException(WRONG_PASSWORD);
        }

        return new SignInResponseInfo(member.getId(), member.getRole());
    }
}
