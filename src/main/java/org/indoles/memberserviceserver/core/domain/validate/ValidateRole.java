package org.indoles.memberserviceserver.core.domain.validate;

import org.indoles.memberserviceserver.core.domain.enums.Role;
import org.indoles.memberserviceserver.global.exception.BadRequestException;

import java.util.Arrays;

import static org.indoles.memberserviceserver.global.exception.ErrorCode.M001;

public class ValidateRole {
    public static Role validate(String userRole) {
        return Arrays.stream(Role.values())
                .filter(role -> role.name().equals(userRole))
                .findAny()
                .orElseThrow(() -> new BadRequestException("사용자의 역할을 찾을 수 없습니다. userRole = " + userRole, M001));
    }
}
