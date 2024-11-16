package org.indoles.memberserviceserver.core.context;

import org.indoles.memberserviceserver.core.infra.MemberCoreRepository;
import org.indoles.memberserviceserver.core.service.MemberService;
import org.indoles.memberserviceserver.core.service.PointService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class ServiceTest {

    @Autowired
    protected DatabaseCleaner databaseCleaner;

    @Autowired
    protected MemberCoreRepository memberCoreRepository;

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected PointService pointService;

    @AfterEach
    void tearDown() {
        databaseCleaner.clear();
    }
}
