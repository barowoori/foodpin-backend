package com.barowoori.foodpinbackend.file.command.domain;

import com.barowoori.foodpinbackend.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FileErrorCode implements ErrorCode {
    MAIL_SEND_FAILED(HttpStatus.BAD_REQUEST, 80000, "MAIL_SEND_FAILED");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    FileErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
