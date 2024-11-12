package org.indoles.memberserviceserver.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.indoles.memberserviceserver.core.domain.Member;
import org.indoles.memberserviceserver.core.domain.Point;
import org.indoles.memberserviceserver.core.entity.MemberEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Mapper {

    public static Member convertToMember(MemberEntity entity) {
        return Member.builder()
                .id(entity.getId())
                .signInId(entity.getSignInId())
                .password(entity.getPassword())
                .role(entity.getRole())
                .point(new Point(entity.getPoint()))
                .build();
    }

    public static MemberEntity convertToMemberEntity(Member member) {
        return MemberEntity.builder()
                .id(member.getId())
                .signInId(member.getSignInId())
                .password(member.getPassword())
                .role(member.getRole())
                .point(member.getPoint().getAmount())
                .build();
    }
}

