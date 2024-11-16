package org.indoles.memberserviceserver.core.dto.response;

import org.indoles.memberserviceserver.core.domain.enums.Role;

public record SignInResponse(
        Role role,
        String accessToken,
        String refreshToken
) {
}

