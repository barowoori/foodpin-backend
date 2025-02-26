package com.barowoori.foodpinbackend.document.command.domain.exception;

import com.barowoori.foodpinbackend.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum DocumentErrorCode implements ErrorCode {
    BUSINESS_REGISTRATION_VALIDATE_FAILED(HttpStatus.BAD_REQUEST, 60000, "BUSINESS_REGISTRATION_VALIDATE_FAILED");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    DocumentErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
