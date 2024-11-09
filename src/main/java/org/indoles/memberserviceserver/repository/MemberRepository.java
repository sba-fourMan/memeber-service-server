package org.indoles.memberserviceserver.repository;

import org.indoles.memberserviceserver.domain.Member;

import java.util.Optional;

public interface MemberRepository {

    boolean isMemberExist(String signInId);

    Member save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findBySignInId(String signInId);
}
