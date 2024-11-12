package org.indoles.memberserviceserver.core.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.memberserviceserver.core.dto.SignUpRequestInfo;
import org.indoles.memberserviceserver.core.entity.MemberEntity;
import org.indoles.memberserviceserver.core.domain.Member;
import org.indoles.memberserviceserver.core.domain.Point;
import org.indoles.memberserviceserver.core.dto.SignInRequestInfo;
import org.indoles.memberserviceserver.core.dto.SignInInfo;
import org.indoles.memberserviceserver.core.repository.MemberRepository;
import org.indoles.memberserviceserver.global.exception.BadRequestException;
import org.indoles.memberserviceserver.global.exception.ErrorCode;
import org.indoles.memberserviceserver.global.exception.NotFoundException;
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
            if (memberRepository.isExist(signUpRequestInfo.signUpId())) {
                throw new BadRequestException("이미 존재하는 아이디입니다. input=" + signUpRequestInfo.signUpId(), ErrorCode.M000);
            }
            Member member = Member.createMemberWithRole(
                    signUpRequestInfo.signUpId(),
                    signUpRequestInfo.password(),
                    signUpRequestInfo.userRole()
            );

            memberRepository.save(member);
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생", e);
            throw e;
        }
    }

    public SignInInfo signIn(SignInRequestInfo signInRequestInfo) {
        try {
            Member member = memberRepository.findBySignInId(signInRequestInfo.signInId())
                    .orElseThrow(() -> new BadRequestException(
                            "아이디에 해당되는 사용자를 찾을 수 없습니다. signInId=" + signInRequestInfo.signInId(),
                            ErrorCode.M002));

            if (!member.confirmPassword(signInRequestInfo.password())) {
                throw new BadRequestException("패스워드가 일치하지 않습니다.", ErrorCode.M003);
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
            if (chargePoint <= 0) {
                throw new BadRequestException("포인트는 0원 이하로 충전할 수 없습니다. 충전 포인트=" + chargePoint, ErrorCode.P005);
            }
            Member member = memberRepository.findById(memberInfo.id())
                    .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다. 사용자 id=" + memberInfo.id(), ErrorCode.M002));

            member.chargePoint(chargePoint);
            memberRepository.save(member);
        } catch (Exception e) {
            log.error("포인트 충전 중 오류 발생", e);
            throw e;
        }
    }

    @Transactional
    public void pointTransfer(long senderId, long recipientId, long amount) {
        try {
            Member sender = findMemberObject(senderId);
            Member recipient = findMemberObject(recipientId);

            sender.pointTransfer(recipient, amount);
            log.debug("  - Member.{}의 포인트 {}원을 Member.{} 에게 전달합니다.", sender.getId(), amount, recipientId);
            log.debug("  - Member.{}의 잔고: {}, Member.{}의 잔고: {}", sender.getId(), sender.getPoint().getAmount(),
                    recipientId, recipient.getPoint().getAmount());

            memberRepository.save(sender);
            memberRepository.save(recipient);
        } catch (Exception e) {
            log.error("포인트 전송 중 오류 발생", e);
            throw e;
        }
    }

    private Member findMemberObject(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다. id=" + id, ErrorCode.M002));
    }
}
