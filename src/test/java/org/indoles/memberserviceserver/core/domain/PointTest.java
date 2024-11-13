package org.indoles.memberserviceserver.core.domain;

import org.indoles.memberserviceserver.global.exception.BadRequestException;
import org.indoles.memberserviceserver.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointTest {

    @Nested
    class plus_Method {

        @Test
        @DisplayName("포인트를 충전할 수 있다.")
        void plus_Point_Success() {
            //given
            Point point = new Point(0);

            //when
            point.plus(10000);

            //then
            assertThat(point.getAmount()).isEqualTo(10000);
        }

        @Test
        @DisplayName("포인트를 최대치만큼 충전하면 예외가 발생된다.")
        void plus_Point_Maximum() {            // given
            Point point = new Point(Long.MAX_VALUE);

            // expect
            assertThatThrownBy(() -> point.plus(1))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("포인트가 최대치를 초과하였습니다.")
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.P006));
        }

        @Test
        @DisplayName("음수로 충전하려고 하면 예외가 발생된다.")
        void plus_Point_Negative() {
            // given
            Point point = new Point(0);

            // expect
            assertThatThrownBy(() -> point.plus(-1))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("금액은 양수여야 합니다.")
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.P008));
        }
    }

    @Nested
    class minus_Method {

        @Test
        @DisplayName("포인트를 사용할 수 있다.")
        void minus_Point_Success() {
            // given
            Point point = new Point(100);

            // when
            point.minus(100);

            // then
            assertThat(point.getAmount()).isEqualTo(0);
        }

        @Test
        @DisplayName("포인트 잔액보다 많은 포인트를 사용하려고 하면 예외가 발생된다.")
        void minux_Point_Over_Fail() {
            // given
            Point point = new Point(100);

            // expect
            assertThatThrownBy(() -> point.minus(101))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("포인트가 부족합니다.")
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.P001));
        }

        @Test
        @DisplayName("음수로 빼려고 하면 예외가 발생된다.")
        void minus_Point_Negative() {
            // given
            Point point = new Point(1000);

            // expect
            assertThatThrownBy(() -> point.minus(-1000))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("금액은 양수여야 합니다.")
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.P008));
        }
    }

    @Test
    @DisplayName("포인트 생성 시 음수일 경우 예외가 발생한다.")
    void create_Point_Negative() {
        // expect
        assertThatThrownBy(() -> new Point(-1))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("금액은 양수여야 합니다.")
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ErrorCode.P008));
    }
}
