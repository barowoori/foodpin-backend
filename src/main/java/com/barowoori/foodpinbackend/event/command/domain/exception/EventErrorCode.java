package com.barowoori.foodpinbackend.event.command.domain.exception;

import com.barowoori.foodpinbackend.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum EventErrorCode implements ErrorCode {
    NOT_FOUND_EVENT(HttpStatus.NOT_FOUND, 40000, "NOT_FOUND_EVENT"),
    EVENT_PHOTO_NOT_FOUND(HttpStatus.NOT_FOUND, 40001, "EVENT_PHOTO_NOT_FOUND");


    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    EventErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
