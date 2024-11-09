package org.indoles.memberserviceserver.dto.response;

import org.indoles.memberserviceserver.entity.enums.Role;

public record SignInResponse(
        Role role
) {
}
