package org.indoles.memberserviceserver.core.dto.request;

import org.indoles.memberserviceserver.core.entity.enums.Role;

public record MemberChargePointRequest(
        Long memberId,
        Role role,
        Long amount
) {
}
