package org.indoles.memberserviceserver.fixture;

import org.indoles.memberserviceserver.core.entity.MemberEntity;

public class MemberFixture {

    public static MemberEntity memberBuild() {
        return MemberEntity.builder()
                .signInId("testId")
                .password("testPassword")
                .role(BUYER)
                .point(100L)
                .build();
    }

    public static MemberEntity membersBuilder(int count) {
        return MemberEntity.builder()
                .signInId("testId" + count)
                .password("testPassword" + count)
                .role(BUYER)
                .point(100L)
                .build();
    }
}
