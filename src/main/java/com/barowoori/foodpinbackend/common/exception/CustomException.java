package com.barowoori.foodpinbackend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpHeaders;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String extraMessage;
    private final HttpHeaders headers;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.extraMessage = null;
        this.headers = HttpHeaders.EMPTY;
    }

    public CustomException(ErrorCode errorCode, String extraMessage) {
        super(errorCode.getMessage() + " " + extraMessage);
        this.errorCode = errorCode;
        this.extraMessage = extraMessage;
        this.headers = HttpHeaders.EMPTY;
    }

    public CustomException(ErrorCode errorCode, String extraMessage, HttpHeaders headers) {
        super(extraMessage == null ? errorCode.getMessage() : errorCode.getMessage() + " " + extraMessage);
        this.errorCode = errorCode;
        this.extraMessage = extraMessage;
        this.headers = headers == null ? HttpHeaders.EMPTY : headers;
    }
}
