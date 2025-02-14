package com.barowoori.foodpinbackend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonErrorCode implements ErrorCode {
    EMPTY_PARAMETER(HttpStatus.BAD_REQUEST, 50000, "EMPTY_PARAMETER");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    CommonErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
