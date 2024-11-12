package org.indoles.memberserviceserver.entity;


import org.indoles.memberserviceserver.core.entity.MemberEntity;
import org.indoles.memberserviceserver.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.indoles.memberserviceserver.core.domain.enums.Role.BUYER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MemberEntityTest {

    @Test
    @DisplayName("MemberEntity가 정상적으로 생성되는지 테스트한다.")
    void createMember_Success(){
        //given
        MemberEntity member = MemberFixture.memberBuild();

        //when&then
        assertThat(member).isNotNull();
        assertEquals(member.getSignInId(), "testId");
        assertEquals(member.getPassword(), "testPassword");
        assertEquals(member.getRole(), BUYER);
        assertEquals(member.getPoint(), 100L);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("여려명의 MemberEntity가 정상적으로 생성되는지 테스트한다.")
    void createMembers_Success(int count){
        //given
        MemberEntity member = MemberFixture.membersBuilder(count);

        //when&then
        assertThat(member).isNotNull();
        assertEquals(member.getSignInId(), "testId" + count);
        assertEquals(member.getPassword(), "testPassword" + count);
        assertEquals(member.getRole(), BUYER);
        assertEquals(member.getPoint(), 100L);
    }
}
