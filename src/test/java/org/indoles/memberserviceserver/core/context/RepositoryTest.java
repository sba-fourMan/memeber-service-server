package org.indoles.memberserviceserver.core.context;

import org.indoles.memberserviceserver.core.infra.MemberCoreRepository;
import org.indoles.memberserviceserver.global.config.JpaConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import({JpaConfig.class, MemberCoreRepository.class})
@DataJpaTest
public abstract class RepositoryTest {
}
