package org.indoles.memberserviceserver.entity.exception;

import org.indoles.memberserviceserver.common.BusinessException;
import org.indoles.memberserviceserver.common.ExceptionCode;

public class MemberException extends BusinessException {

    public MemberException(ExceptionCode exceptionCode, Object... args) {
        super(exceptionCode, args);
    }
}
