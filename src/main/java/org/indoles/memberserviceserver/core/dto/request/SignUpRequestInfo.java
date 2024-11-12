package org.indoles.memberserviceserver.core.dto.request;

import org.indoles.memberserviceserver.core.entity.exception.MemberException;
import org.indoles.memberserviceserver.core.entity.exception.MemberExceptionCode;

import java.util.Objects;

public record SignUpRequestInfo(
        String signUpId,
        String password,
        String userRole
) {

    public SignUpRequestInfo {
        validateNotNull(signUpId, "회원가입 ID");
        validateNotNull(password, "회원가입 패스워드");
        validateNotNull(userRole, "사용자 역할");
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new MemberException(MemberExceptionCode.FIELD_CANNOT_BE_NULL, fieldName, fieldName);
        }
    }
}
