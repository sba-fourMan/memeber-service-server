package org.indoles.memberserviceserver.core.repository;

import org.indoles.memberserviceserver.core.domain.Member;
import org.indoles.memberserviceserver.core.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository {

    boolean isExist(String signInId);

    Member save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findBySignInId(String signInId);
}

