package org.indoles.memberserviceserver.dto.request;

import org.indoles.memberserviceserver.entity.enums.Role;

public record MemberChargePointRequest(
        Long memberId,
        Role role,
        Long amount
) {
}
