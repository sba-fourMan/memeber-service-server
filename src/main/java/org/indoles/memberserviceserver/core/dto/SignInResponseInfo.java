package org.indoles.memberserviceserver.core.dto;

import org.indoles.memberserviceserver.core.domain.enums.Role;

public record SignInResponseInfo(
        Role role
) {
}

