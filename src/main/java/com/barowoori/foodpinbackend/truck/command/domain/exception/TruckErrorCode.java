package com.barowoori.foodpinbackend.truck.command.domain.exception;

import com.barowoori.foodpinbackend.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TruckErrorCode implements ErrorCode {
    MEMBER_NICKNAME_EMPTY(HttpStatus.BAD_REQUEST, 20000, "MEMBER_NICKNAME_EMPTY");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    TruckErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
