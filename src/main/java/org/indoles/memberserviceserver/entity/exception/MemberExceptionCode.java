package org.indoles.memberserviceserver.entity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.indoles.memberserviceserver.common.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum MemberExceptionCode implements ExceptionCode {

    ROLE_NOT_FOUND(NOT_FOUND, "MEM-001", "사용자의 Role을 찾을 수 없음");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
