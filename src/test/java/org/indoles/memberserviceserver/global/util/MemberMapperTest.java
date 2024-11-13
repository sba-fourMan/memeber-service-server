package org.indoles.memberserviceserver.global.util;

import org.indoles.memberserviceserver.core.domain.Member;
import org.indoles.memberserviceserver.core.domain.Point;
import org.indoles.memberserviceserver.core.entity.MemberEntity;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.indoles.memberserviceserver.core.domain.enums.Role.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemberMapperTest {
    @Nested
    class MemberMapperEntity_Success {
        @Test
        public void 도메인_엔티티로_변환하면_정보가_동일하다() {
            // given
            MemberEntity entity = MemberEntity.builder()
                    .id(1L)
                    .signInId("helloworld")
                    .password("password1234")
                    .role(BUYER)
                    .point(12345L)
                    .build();

            // when
            Member member = Mapper.convertToMember(entity);

            //then
            assertAll(
                    () -> assertThat(member.getId()).isEqualTo(1L),
                    () -> assertThat(member.getSignInId()).isEqualTo("helloworld"),
                    () -> assertThat(member.getPassword()).isEqualTo("password1234"),
                    () -> assertThat(member.getRole()).isEqualTo(BUYER),
                    () -> assertThat(member.getPoint().getAmount()).isEqualTo(12345L)
            );
        }

        @Test
        void 영속성_엔티티로_변환하면_정보가_동일하다() {
            // given
            Member member = Member.builder()
                    .id(1L)
                    .signInId("helloworld")
                    .password("password1234")
                    .role(BUYER)
                    .point(new Point(12345))
                    .build();

            // when
            MemberEntity entity = Mapper.convertToMemberEntity(member);

            // then
            assertAll(
                    () -> assertThat(entity.getId()).isEqualTo(1L),
                    () -> assertThat(entity.getSignInId()).isEqualTo("helloworld"),
                    () -> assertThat(entity.getPassword()).isEqualTo("password1234"),
                    () -> assertThat(entity.getRole()).isEqualTo(BUYER),
                    () -> assertThat(entity.getPoint()).isEqualTo(12345L)
            );
        }
    }
}
