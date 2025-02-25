package com.barowoori.foodpinbackend.region.command.domain.exception;

import com.barowoori.foodpinbackend.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RegionErrorCode implements ErrorCode {
    NOT_CORRECT_REGION_CODE_PATTERN(HttpStatus.BAD_REQUEST, 70000, "NOT_CORRECT_REGION_CODE_PATTERN");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    RegionErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
