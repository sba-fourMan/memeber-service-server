package org.indoles.memberserviceserver.core.dto.request;

import static org.indoles.memberserviceserver.core.dto.validateDto.ValidateMemberDto.validateNotNull;

public record SignInRequest(
        String signInId,
        String password
) {
    public SignInRequest {
        validateNotNull(signInId, "로그인 ID");
        validateNotNull(password, "로그인 패스워드");
    }
}
