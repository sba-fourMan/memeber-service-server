package org.indoles.memberserviceserver.core.domain;

import org.indoles.memberserviceserver.core.domain.enums.Role;
import org.indoles.memberserviceserver.global.exception.BadRequestException;
import org.indoles.memberserviceserver.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class RoleTest {

    @Test
    @DisplayName("사용자의 Role을 찾을 수 있다.")
    void find_Role_Success() {
        // given
        String seller = "SELLER";
        String buyer = "BUYER";

        //then
        assertAll(
                () -> assertThat(Role.find(seller)).isEqualTo(Role.SELLER),
                () -> assertThat(Role.find(buyer)).isEqualTo(Role.BUYER)
        );
    }

    @Test
    @DisplayName("사용자의 Role을 찾을 수 없다면 에러가 발생한다.")
    void find_Role_Fail() {
        //given
        String invalidValue = "invalidValue";

        //then
        assertThatThrownBy(() -> Role.find(invalidValue))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("사용자의 역할을 찾을 수 없습니다. userRole = invalidValue")
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ErrorCode.M001));
    }
}
