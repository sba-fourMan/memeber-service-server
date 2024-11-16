package org.indoles.memberserviceserver.core.service;

import org.indoles.memberserviceserver.core.context.ServiceTest;
import org.indoles.memberserviceserver.core.domain.Member;
import org.indoles.memberserviceserver.core.domain.enums.Role;
import org.indoles.memberserviceserver.core.dto.SignInInfo;
import org.indoles.memberserviceserver.core.dto.SignInRequestInfo;
import org.indoles.memberserviceserver.core.dto.SignUpRequestInfo;
import org.indoles.memberserviceserver.global.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MemberServiceTest extends ServiceTest {

    @Nested
    class signUp_Method {


        @Test
        @DisplayName("정상적인 회원가입 요청이 입력되면 회원가입이 완료된다.")
        void signUp_Success() {
            // given
            SignUpRequestInfo signUpRequestInfo = new SignUpRequestInfo("testId", "securePassword00", "BUYER");

            // when
            memberService.signUp(signUpRequestInfo);

            // then
            assertThat(memberCoreRepository.findBySignInId("testId")).isPresent();
        }


        @Test
        @DisplayName("이미 존재하는 아이디로 회원가입을 시도하면 예외가 발생한다.")
        void signUp_AlreadyExistId_ThrowException() {
            // given
            Member member = Member.createMemberWithRole("testId", "securePassword00", "BUYER");
            SignUpRequestInfo signUpRequestInfo = new SignUpRequestInfo(member.getSignInId(), member.getPassword(), member.getRole().name());
            memberService.signUp(signUpRequestInfo);

            // expect
            assertThatThrownBy(() -> memberService.signUp(signUpRequestInfo))
                    .isInstanceOf(BadRequestException.class);
        }
    }

    @Nested
    class signIn_Method {

        @Test
        @DisplayName("정상적인 로그인 요청이 입력되면 로그인이 완료된다.")
        void signIn_Success() {
            // given
            Member member = Member.createMemberWithRole("testId", "securePassword00", "BUYER");
            SignUpRequestInfo signUpRequestInfo = new SignUpRequestInfo(member.getSignInId(), member.getPassword(), member.getRole().name());
            memberService.signUp(signUpRequestInfo);

            SignInRequestInfo signInRequestInfo = new SignInRequestInfo("testId", "securePassword00");

            // when
            SignInInfo signInInfo = memberService.signIn(signInRequestInfo);

            // then
            assertThat(signInInfo).isNotNull();
            assertThat(signInInfo.id()).isNotNull();
            assertThat(signInInfo.role()).isEqualTo(Role.BUYER);
        }

        @Test
        @DisplayName("존재하지 않는 아이디로 로그인을 시도하면 예외가 발생한다.")
        void signIn_NotExistId_ThrowException() {
            // given
            SignInRequestInfo signInRequestInfo = new SignInRequestInfo("testId", "securePassword00");

            // expect
            assertThatThrownBy(() -> memberService.signIn(signInRequestInfo))
                    .isInstanceOf(BadRequestException.class);
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않는 경우 로그인을 시도하면 예외가 발생한다.")
        void signIn_NotMatchPassword_ThrowException() {
            // given
            Member member = Member.createMemberWithRole("testId", "correctPassword00", "BUYER");
            SignUpRequestInfo signUpRequestInfo = new SignUpRequestInfo(member.getSignInId(), member.getPassword(), member.getRole().name());
            memberService.signUp(signUpRequestInfo);

            SignInRequestInfo signInRequestInfo = new SignInRequestInfo("testId", "wrongPassword00");

            // expect
            assertThatThrownBy(() -> memberService.signIn(signInRequestInfo))
                    .isInstanceOf(BadRequestException.class);
        }
    }
}
