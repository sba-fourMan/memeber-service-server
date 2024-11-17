package org.indoles.memberserviceserver.core.service;

import org.indoles.memberserviceserver.core.context.ServiceTest;
import org.indoles.memberserviceserver.core.domain.Member;
import org.indoles.memberserviceserver.core.domain.enums.Role;
import org.indoles.memberserviceserver.core.dto.response.SignInfoRequest;
import org.indoles.memberserviceserver.core.fixture.MemberFixture;
import org.indoles.memberserviceserver.global.exception.BadRequestException;
import org.indoles.memberserviceserver.global.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PointServiceTest extends ServiceTest {

    @Nested
    class chargePoint_Method {

        @Test
        @DisplayName("정상적으로 포인트를 충전할 수 있다.")
        void chargePont_Success() {
            // given
            Member member = MemberFixture.createBuyerWithDefaultPoint();
            memberCoreRepository.save(member);

            ReflectionTestUtils.setField(member, "id", 1L);

            Long id = member.getId();
            Role role = member.getRole();
            Long point = 1000L;

            // when
            pointService.chargePoint(new SignInfoRequest(id, role), point);

            // then
            assertThat(memberCoreRepository.findById(id).get().getPoint().getAmount()).isEqualTo(2000L);
        }

        @Test
        @DisplayName("포인트 충전 시 사용자를 찾을 수 없으면 예외가 발생한다.")
        void chargePoint_ThrowException_IfMemberNotFound() {
            // given
            Long id = 99999L;
            Long point = 1000L;

            // expect
            assertThatThrownBy(() -> pointService.chargePoint(new SignInfoRequest(id, Role.BUYER), point))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("포인트 충전 시 포인트가 음수이면 예외가 발생한다.")
        void chargePoint_ThrowException_IfPointIsNegative() {
            // given
            Member member = MemberFixture.createBuyerWithDefaultPoint();
            memberCoreRepository.save(member);

            ReflectionTestUtils.setField(member, "id", 1L);

            Long id = member.getId();
            Role role = member.getRole();
            Long point = -100L;


            // expect
            assertThatThrownBy(() -> pointService.chargePoint(new SignInfoRequest(id, role), point))
                    .isInstanceOf(BadRequestException.class);
        }
    }

    @Nested
    class pointTransfer_Method {

        @Test
        @DisplayName("정상적으로 포인트를 전송할 수 있다.")
        void pointTransfer_Success() {
            // given
            Member sender = MemberFixture.createBuyerWithDefaultPoint();
            Member recipient = MemberFixture.createSellerWithDefaultPoint();
            memberCoreRepository.save(sender);
            memberCoreRepository.save(recipient);

            ReflectionTestUtils.setField(sender, "id", 1L);
            ReflectionTestUtils.setField(recipient, "id", 2L);

            Long senderId = sender.getId();
            Long recipientId = recipient.getId();
            Long amount = 500L;

            // when
            pointService.pointTransfer(senderId, recipientId, amount);

            // then
            assertThat(memberCoreRepository.findById(senderId).get().getPoint().getAmount()).isEqualTo(500L);
            assertThat(memberCoreRepository.findById(recipientId).get().getPoint().getAmount()).isEqualTo(1500L);
        }

        @Test
        @DisplayName("포인트 전송 시 보내는 사용자를 찾을 수 없으면 예외가 발생한다.")
        void pointTransfer_NotFoundSender_ThrowException() {
            // given
            Member recipient = MemberFixture.createSellerWithDefaultPoint();

            memberCoreRepository.save(recipient);

            ReflectionTestUtils.setField(recipient, "id", 1L);

            Long senderId = 99999L;
            Long recipientId = recipient.getId();
            Long amount = 500L;


            // expect
            assertThatThrownBy(() -> pointService.pointTransfer(senderId, recipientId, amount))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("포인트 전송 시 받는 사용자를 찾을 수 없으면 예외가 발생한다.")
        void pointTransfer_NotFoundRecipient_ThrowException() {
            // given
            Member sender = MemberFixture.createBuyerWithDefaultPoint();
            memberCoreRepository.save(sender);

            ReflectionTestUtils.setField(sender, "id", 1L);

            Long senderId = sender.getId();
            Long recipientId = 99999L;
            Long amount = 500L;

            // expect
            assertThatThrownBy(() -> pointService.pointTransfer(senderId, recipientId, amount))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    class refundPoint_Method {

        @Test
        @DisplayName("정상적으로 포인트를 환불할 수 있다.")
        void refundPoint_Success() {
            // given
            Member sender = MemberFixture.createBuyerWithDefaultPoint();
            Member recipient = MemberFixture.createSellerWithDefaultPoint();
            memberCoreRepository.save(sender);
            memberCoreRepository.save(recipient);

            ReflectionTestUtils.setField(sender, "id", 1L);
            ReflectionTestUtils.setField(recipient, "id", 2L);

            Long senderId = sender.getId();
            Long recipientId = recipient.getId();
            Long amount = 500L;

            // when
            pointService.refundPoint(senderId, recipientId, amount);

            // then
            assertThat(memberCoreRepository.findById(senderId).get().getPoint().getAmount()).isEqualTo(500L);
            assertThat(memberCoreRepository.findById(recipientId).get().getPoint().getAmount()).isEqualTo(1500L);
        }

        @Test
        @DisplayName("포인트 환불 시 보내는 사용자를 찾을 수 없으면 예외가 발생한다.")
        void refoundPoint_NotFoundSender_ThrowException() {
            // given
            Member recipient = MemberFixture.createSellerWithDefaultPoint();
            memberCoreRepository.save(recipient);

            ReflectionTestUtils.setField(recipient, "id", 1L);

            Long senderId = 99999L;
            Long recipientId = recipient.getId();
            Long amount = 500L;

            // expect
            assertThatThrownBy(() -> pointService.refundPoint(senderId, recipientId, amount))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("포인트 환불 시 받는 사용자를 찾을 수 없으면 예외가 발생한다.")
        void refoundPoint_NotFoundRecipient_ThrowException() {
            // given
            Member sender = MemberFixture.createBuyerWithDefaultPoint();
            memberCoreRepository.save(sender);

            ReflectionTestUtils.setField(sender, "id", 1L);

            Long senderId = sender.getId();
            Long recipientId = 99999L;
            Long amount = 500L;

            // expect
            assertThatThrownBy(() -> pointService.refundPoint(senderId, recipientId, amount))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
