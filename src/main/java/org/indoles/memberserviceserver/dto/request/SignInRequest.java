package org.indoles.memberserviceserver.dto.request;

import org.indoles.memberserviceserver.entity.exception.MemberException;
import org.indoles.memberserviceserver.entity.exception.MemberExceptionCode;

import java.util.Objects;

public record SignInRequest(String signInId, String password) {

    public SignInRequest {
        validateNotNull(signInId, "아이디");
        validateNotNull(password, "비밀번호");
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new MemberException(MemberExceptionCode.FIELD_CANNOT_BE_NULL, fieldName);
        }
    }
}
