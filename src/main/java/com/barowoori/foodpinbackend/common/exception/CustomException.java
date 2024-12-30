package com.barowoori.foodpinbackend.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String extraMessage;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.extraMessage = null;
    }

    public CustomException(ErrorCode errorCode, String extraMessage) {
        super(errorCode.getMessage() + " " + extraMessage);
        this.errorCode = errorCode;
        this.extraMessage = extraMessage;
    }
}