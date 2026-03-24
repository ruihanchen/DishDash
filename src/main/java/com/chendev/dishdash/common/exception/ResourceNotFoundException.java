package com.chendev.dishdash.common.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public ResourceNotFoundException(ErrorCode errorCode, String detail) {
        super(errorCode.getDefaultMessage() + " (" + detail + ")");
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
