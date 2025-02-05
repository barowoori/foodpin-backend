package com.barowoori.foodpinbackend.truck.command.domain.exception;

import com.barowoori.foodpinbackend.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TruckErrorCode implements ErrorCode {
    NOT_FOUND_TRUCK(HttpStatus.BAD_REQUEST, 30000, "NOT_FOUND_TRUCK"),
    TRUCK_PHOTO_NOT_FOUND(HttpStatus.NOT_FOUND, 30001, "TRUCK_PHOTO_NOT_FOUND"),
    TRUCK_MENU_PHOTO_NOT_FOUND(HttpStatus.NOT_FOUND, 30002, "TRUCK_MENU_PHOTO_NOT_FOUND"),
    TRUCK_DOCUMENT_PHOTO_NOT_FOUND(HttpStatus.NOT_FOUND, 30003, "TRUCK_DOCUMENT_PHOTO_NOT_FOUND"),
    TRUCK_DOCUMENT_PHOTO_EMPTY(HttpStatus.BAD_REQUEST, 30004, "TRUCK_DOCUMENT_PHOTO_EMPTY");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    TruckErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
