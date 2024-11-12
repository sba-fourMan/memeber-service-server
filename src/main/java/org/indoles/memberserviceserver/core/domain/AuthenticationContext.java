package org.indoles.memberserviceserver.core.domain;

import lombok.Getter;
import org.indoles.memberserviceserver.core.dto.SignInInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@Component
@RequestScope
public class AuthenticationContext {

    private SignInInfo principal;

    public void setPrincipal(SignInInfo principal) {
        this.principal = principal;
    }
}
