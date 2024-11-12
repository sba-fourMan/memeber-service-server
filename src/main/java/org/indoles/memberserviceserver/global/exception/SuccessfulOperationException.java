package org.indoles.memberserviceserver.global.exception;

public class SuccessfulOperationException extends CustomException {

    public SuccessfulOperationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
