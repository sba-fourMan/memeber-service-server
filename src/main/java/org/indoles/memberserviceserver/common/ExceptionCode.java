package org.indoles.memberserviceserver.common;

import org.springframework.http.HttpStatus;

public interface ExceptionCode {

    HttpStatus getHttpStatus();

    Integer getCode();

    String getMessage();
}
