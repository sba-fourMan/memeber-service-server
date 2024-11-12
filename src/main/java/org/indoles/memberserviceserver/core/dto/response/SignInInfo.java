package org.indoles.memberserviceserver.core.dto.response;

import org.indoles.memberserviceserver.core.entity.enums.Role;
import org.indoles.memberserviceserver.core.entity.exception.MemberException;
import org.indoles.memberserviceserver.core.entity.exception.MemberExceptionCode;

import java.util.Objects;

public record SignInInfo(Long id, Role role) {

    public SignInInfo {
        validateNotNull(id, "로그인한 사용자의 식별자");
        validateNotNull(role, "로그인한 사용자의 역할");
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new MemberException(MemberExceptionCode.FIELD_CANNOT_BE_NULL, fieldName);
        }
    }

    public boolean isType(Role role) {
        return this.role.equals(role);
    }

}
