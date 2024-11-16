package org.indoles.memberserviceserver.core.context;

import org.indoles.memberserviceserver.core.infra.MemberCoreRepository;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import({MemberCoreRepository.class})
@DataJpaTest
public abstract class RepositoryTest {
}
