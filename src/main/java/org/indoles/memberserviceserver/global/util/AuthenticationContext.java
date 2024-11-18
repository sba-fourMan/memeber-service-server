package org.indoles.memberserviceserver.global.util;

import lombok.Getter;
import org.indoles.memberserviceserver.core.dto.request.SignInfoRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@Component
@RequestScope
public class AuthenticationContext {

    private SignInfoRequest principal;

    public void setPrincipal(SignInfoRequest principal) {
        this.principal = principal;
    }
}
