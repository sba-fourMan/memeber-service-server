package org.indoles.memberserviceserver.core.repository;

import org.indoles.memberserviceserver.core.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    boolean existsBySignInId(String signInId);

    Optional<MemberEntity> findById(Long id);

    Optional<MemberEntity> findBySignInId(String signInId);
}
