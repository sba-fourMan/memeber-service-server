package org.indoles.memberserviceserver.entity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.indoles.memberserviceserver.common.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum MemberExceptionCode implements ExceptionCode {

    ROLE_NOT_FOUND(NOT_FOUND, "MEM-001", "사용자의 Role을 찾을 수 없음"),
    POINT_NOT_ENOUGH(BAD_REQUEST, "MEM-002", "포인트가 부족합니다"),
    POINT_OVER_MAX(BAD_REQUEST, "MEM-003", "포인트가 최대치를 초과했습니다"),
    NUMBER_NOT_POSITIVE(BAD_REQUEST, "MEM-004", "포인트는 양수여야 합니다"),
    ;

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
