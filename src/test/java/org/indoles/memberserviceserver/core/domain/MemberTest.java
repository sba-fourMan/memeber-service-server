package org.indoles.memberserviceserver.core.domain;

import org.indoles.memberserviceserver.core.domain.enums.Role;
import org.indoles.memberserviceserver.core.fixture.MemberFixture;
import org.indoles.memberserviceserver.global.exception.BadRequestException;
import org.indoles.memberserviceserver.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.indoles.memberserviceserver.core.domain.enums.Role.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemberTest {


    @Test
    @DisplayName("정상적으로 회원을 생성할 수 있다.")
    void createMember_Success() {
        // when
        Member member = Member.builder()
                .signInId("testId")
                .password("password00")
                .role(BUYER)
                .point(new Point(100))
                .build();

        // then
        assertAll(
                () -> assertThat(member.getSignInId()).isEqualTo("testId"),
                () -> assertThat(member.getPassword()).isEqualTo("password00"),
                () -> assertThat(member.getRole()).isEqualTo(BUYER),
                () -> assertThat(member.getPoint()).isEqualTo(new Point(100))
        );
    }

    @Nested
    class validatePassword_Method {

        @ParameterizedTest
        @MethodSource("generateInvalidPassword")
        @DisplayName("유효하지 않은 비밀번호면 예외가 발생한다.")
        void invalidPassword_Then_ThrowException(String expectedMessage, String password, ErrorCode expectedErrorCode) {
            // expect
            assertThatThrownBy(() -> Member.builder()
                    .signInId("testId")
                    .password(password)
                    .role(Role.BUYER)
                    .point(new Point(100))
                    .build())
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(expectedMessage)
                    .hasFieldOrPropertyWithValue("errorCode", expectedErrorCode);
        }

        private static Stream<Arguments> generateInvalidPassword() {
            return Stream.of(
                    Arguments.of("비밀번호는 빈칸 또는 공백일 수 없습니다.", "", ErrorCode.M006),
                    Arguments.of("비밀번호는 8자 이상 20자 이하로 입력해주세요. 현재 길이=3", "p00", ErrorCode.M007),
                    Arguments.of("비밀번호는 8자 이상 20자 이하로 입력해주세요. 현재 길이=26", "passwordpasswordpassword00", ErrorCode.M007),
                    Arguments.of("비밀번호는 숫자가 반드시 포함되어야 합니다.", "password", ErrorCode.M008),
                    Arguments.of("비밀번호는 알파벳 소문자가 반드시 포함되어야 합니다.", "PASSWORD00", ErrorCode.M009),
                    Arguments.of("비밀번호는 영문자와 숫자만 사용할 수 있습니다.", "password00!", ErrorCode.M010)
            );
        }
    }

    @Test
    @DisplayName("포인트를 사용할 수 있다.")
    void usePoint_Success() {
        // given
        Member buyer = Member.builder()
                .signInId("testId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(100))
                .build();
        long price = 10L;
        long quantity = 10L;

        // when
        buyer.usePoint(price * quantity);

        // then
        assertThat(buyer.getPoint()).isEqualTo(new Point(0));
    }

    @Test
    @DisplayName("포인트 잔액보다 많은 양을 사용하려하면 예외가 발생한다.")
    void usePoint_Over_Than_PointAmount_Then_ThrowException() {
        // given
        Member buyer = Member.builder()
                .signInId("testId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(100))
                .build();
        long price = 10L;
        long quantity = 11L;

        // expect
        assertThatThrownBy(() -> buyer.usePoint(price * quantity))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("포인트가 부족합니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.P001);
    }

    @Test
    @DisplayName("포인트를 충전할 수 있다.")
    void chargePoint_Success() {
        // given
        Member buyer = Member.builder()
                .signInId("testId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(100))
                .build();

        // when
        buyer.chargePoint(100);

        // then
        assertThat(buyer.getPoint()).isEqualTo(new Point(200));
    }

    @Nested
    class confirmPassword_Method {

        @Test
        @DisplayName("비밀번호가 맞는 경우 true를 반환한다.")
        void Password_Is_Correct_Then_Return_True() {
            // given
            Member buyer = Member.builder()
                    .signInId("testId")
                    .password("password00")
                    .role(Role.BUYER)
                    .point(new Point(100))
                    .build();

            // then
            assertThat(buyer.confirmPassword("password00")).isTrue();
        }

        @Test
        @DisplayName("비밀번호가 틀린 경우 false를 반환한다.")
        void Password_Is_Incorrect_Then_Return_False() {
            // given
            Member buyer = Member.builder()
                    .signInId("testId")
                    .password("password00")
                    .role(Role.BUYER)
                    .point(new Point(100))
                    .build();

            // then
            assertThat(buyer.confirmPassword("password1")).isFalse();
        }
    }

    @Nested
    @DisplayName("구매자라면 true를 반환한다.")
    class isBuyer_Method {

        @Test
        void Is_Buyer_Then_Return_True() {
            // given
            Member buyer = Member.builder()
                    .signInId("testId")
                    .password("password00")
                    .role(Role.BUYER)
                    .point(new Point(100))
                    .build();

            // then
            assertThat(buyer.isBuyer()).isTrue();
        }

        @Test
        @DisplayName("판매자인 경우 false를 반환한다.")
        void Is_Seller_Then_Return_False() {
            // given
            Member seller = Member.builder()
                    .signInId("testId")
                    .password("password00")
                    .role(Role.SELLER)
                    .point(new Point(100))
                    .build();

            // then
            assertThat(seller.isBuyer()).isFalse();
        }
    }

    @Test
    @DisplayName("동일한 회원인지 확인할 수 있다.")
    void Check_Same_Member_Then_Return_True() {
        // given
        Member member = Member.builder()
                .signInId("testId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(100))
                .build();

        // when
        boolean isSameMember = member.isSameMember("testId");

        // then
        assertThat(isSameMember).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    "})
    @DisplayName("아이디가 빈칸 혹은 공백이라면 예외가 발생한다.")
    void ID_Should_Not_Be_Empty(String userId) {
        // expect
        assertThatThrownBy(() ->
                Member.builder()
                        .signInId(userId)
                        .password("password1234")
                        .role(Role.BUYER)
                        .point(new Point(100))
                        .build())
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.M004);
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "b", "21ccccc21212121212121"})
    @DisplayName("아이디는 20자 이상으로 입력할 경우 예외가 발생한다.")
    void ID_Should_Be_Within_20_Characters(String userId) {
        // expect
        assertThatThrownBy(() ->
                Member.builder()
                        .signInId(userId)
                        .password("password1234")
                        .role(Role.BUYER)
                        .point(new Point(100))
                        .build())
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.M005);
    }

    @Nested
    class pointTransfer_Method {

        @Test
        @DisplayName("포인트를 다른 회원에게 송금할 수 있다.")
        void Transfer_Point_To_Another_Member() {
            // given
            Member buyer = MemberFixture.createBuyerWithDefaultPoint();
            Member seller = MemberFixture.createSellerWithDefaultPoint();

            // when
            buyer.pointTransfer(seller, 500);

            // then
            assertThat(buyer.getPoint()).isEqualTo(new Point(500));
            assertThat(seller.getPoint()).isEqualTo(new Point(1500));
        }

        @Test
        @DisplayName("포인트가 부족한 경우 예외가 발생한다.")
        void Point_Is_Not_Enough_Then_Throw_Exception() {
            // given
            Member buyer = MemberFixture.createBuyerWithDefaultPoint();

            // expect
            assertThatThrownBy(() -> buyer.pointTransfer(buyer, 1234567890))
                    .isInstanceOf(BadRequestException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.P001);
        }

    }
}
