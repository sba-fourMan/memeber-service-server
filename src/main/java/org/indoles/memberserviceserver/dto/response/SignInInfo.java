package org.indoles.memberserviceserver.dto.response;

import org.indoles.memberserviceserver.entity.enums.Role;
import org.indoles.memberserviceserver.entity.exception.MemberException;

import java.util.Objects;

import static org.indoles.memberserviceserver.entity.exception.MemberExceptionCode.*;

public record SignInInfo(Long id, Role role) {

    public SignInInfo {
        validateNotNull(id, "로그인한 사용자의 식별자");
        validateNotNull(role, "로그인한 사용자의 역할");
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new MemberException(FIELD_CANNOT_BE_NULL, fieldName);
        }
    }

    public boolean isType(Role role) {
        return this.role.equals(role);
    }

}
