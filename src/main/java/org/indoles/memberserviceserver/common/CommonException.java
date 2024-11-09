package org.indoles.memberserviceserver.common;

public class CommonException extends BusinessException {

    public CommonException(ExceptionCode exceptionCode, Object... args) {
        super(exceptionCode, args);
    }
}
