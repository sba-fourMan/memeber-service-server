package org.indoles.memberserviceserver.global.util;

import org.indoles.memberserviceserver.core.domain.Member;
import org.indoles.memberserviceserver.core.domain.Point;
import org.indoles.memberserviceserver.core.entity.MemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.indoles.memberserviceserver.core.domain.enums.Role.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemberMapperTest {

    @Test
    @DisplayName("엔티티를 도메인으로 변환하면 정보가 동일하다")
    void transfer_MemberEntity_ToMember() {
        // given
        MemberEntity entity = MemberEntity.builder()
                .id(1L)
                .signInId("testId")
                .password("password1234")
                .role(BUYER)
                .point(12345L)
                .build();

        // when
        Member member = Mapper.convertToMember(entity);

        //then
        assertAll(
                () -> assertThat(member.getId()).isEqualTo(1L),
                () -> assertThat(member.getSignInId()).isEqualTo("testId"),
                () -> assertThat(member.getPassword()).isEqualTo("password1234"),
                () -> assertThat(member.getRole()).isEqualTo(BUYER),
                () -> assertThat(member.getPoint().getAmount()).isEqualTo(12345L)
        );
    }

    @Test
    @DisplayName("도메인을 엔티티로 변환하면 정보가 동일하다")
    void transfer_Member_ToMemberEntity() {
        // given
        Member member = Member.builder()
                .id(1L)
                .signInId("testId")
                .password("password1234")
                .role(BUYER)
                .point(new Point(12345))
                .build();

        // when
        MemberEntity entity = Mapper.convertToMemberEntity(member);

        // then
        assertAll(
                () -> assertThat(entity.getId()).isEqualTo(1L),
                () -> assertThat(entity.getSignInId()).isEqualTo("testId"),
                () -> assertThat(entity.getPassword()).isEqualTo("password1234"),
                () -> assertThat(entity.getRole()).isEqualTo(BUYER),
                () -> assertThat(entity.getPoint()).isEqualTo(12345L)
        );
    }
}
