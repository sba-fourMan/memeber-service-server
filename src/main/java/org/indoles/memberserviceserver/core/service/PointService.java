package org.indoles.memberserviceserver.core.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.memberserviceserver.core.domain.Member;
import org.indoles.memberserviceserver.core.dto.request.SignInfoRequest;
import org.indoles.memberserviceserver.core.infra.MemberCoreRepository;
import org.indoles.memberserviceserver.global.exception.BadRequestException;
import org.indoles.memberserviceserver.global.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.indoles.memberserviceserver.global.exception.ErrorCode.M002;
import static org.indoles.memberserviceserver.global.exception.ErrorCode.P005;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointService {

    private final MemberCoreRepository memberCoreRepository;

    @Transactional
    public void chargePoint(SignInfoRequest memberInfo, long chargePoint) {
        try {
            if (chargePoint <= 0) {
                throw new BadRequestException("포인트는 0원 이하로 충전할 수 없습니다. 충전 포인트=" + chargePoint, P005);
            }
            Member member = memberCoreRepository.findById(memberInfo.id())
                    .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다. 사용자 id=" + memberInfo.id(), M002));

            member.chargePoint(chargePoint);
            memberCoreRepository.save(member);
        } catch (Exception e) {
            log.error("포인트 충전 중 오류 발생", e);
            throw e;
        }
    }

    @Transactional
    public void pointTransfer(long senderId, long receiverId, long amount) {
        try {
            Member sender = findMemberObject(senderId);
            Member recipient = findMemberObject(receiverId);

            sender.pointTransfer(recipient, amount);
            log.debug("  - Member.{}의 포인트 {}원을 Member.{} 에게 전달합니다.", sender.getId(), amount, receiverId);
            log.debug("  - Member.{}의 잔고: {}, Member.{}의 잔고: {}", sender.getId(), sender.getPoint().getAmount(),
                    receiverId, recipient.getPoint().getAmount());

            memberCoreRepository.save(sender);
            memberCoreRepository.save(recipient);
        } catch (Exception e) {
            log.error("포인트 전송 중 오류 발생", e);
            throw e;
        }
    }

    private Member findMemberObject(Long id) {
        return memberCoreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다. id=" + id, M002));
    }

    @Transactional
    public void refundPoint(long senderId, long receiverId, long amount) {
        try {
            Member sender = findMemberObject(senderId);
            Member recipient = findMemberObject(receiverId);

            sender.refundPoint(recipient, amount);
            log.debug("  - Member.{}의 포인트 {}원을 Member.{} 에게 환불합니다.", sender.getId(), amount, receiverId);
            log.debug("  - Member.{}의 잔고: {}, Member.{}의 잔고: {}", sender.getId(), sender.getPoint().getAmount(),
                    receiverId, recipient.getPoint().getAmount());

            memberCoreRepository.save(sender);
            memberCoreRepository.save(recipient);
        } catch (Exception e) {
            log.error("포인트 환불 중 오류 발생", e);
            throw e;
        }
    }

    public Long getRemainingPoints(Long memberId) {
        Member member = findMemberObject(memberId);
        return member.getPoint().getAmount();
    }
}
