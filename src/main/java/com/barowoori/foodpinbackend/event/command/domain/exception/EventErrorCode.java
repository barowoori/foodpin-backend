package com.barowoori.foodpinbackend.event.command.domain.exception;

import com.barowoori.foodpinbackend.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum EventErrorCode implements ErrorCode {
    NOT_FOUND_EVENT(HttpStatus.NOT_FOUND, 40000, "NOT_FOUND_EVENT"),
    EVENT_PHOTO_NOT_FOUND(HttpStatus.NOT_FOUND, 40001, "EVENT_PHOTO_NOT_FOUND"),
    EVENT_REGION_NOT_FOUND(HttpStatus.NOT_FOUND, 40002, "EVENT_REGION_NOT_FOUND"),
    EVENT_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, 40003, "EVENT_CATEGORY_NOT_FOUND"),
    EVENT_RECRUIT_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, 40004, "EVENT_RECRUIT_DETAIL_NOT_FOUND"),
    NOT_EVENT_CREATOR(HttpStatus.BAD_REQUEST, 40005, "NOT_EVENT_CREATOR"),
    EVENT_DATE_NOT_FOUND(HttpStatus.NOT_FOUND, 40006, "EVENT_DATE_NOT_FOUND"),
    ALREADY_APPLIED_EVENT(HttpStatus.BAD_REQUEST, 40007, "ALREADY_APPLIED_EVENT"),
    EVENT_NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, 40008, "EVENT_NOTICE_NOT_FOUND"),
    EVENT_APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, 40009, "EVENT_APPLICATION_NOT_FOUND"),
    EVENT_DATE_EMPTY(HttpStatus.BAD_REQUEST, 40010, "EVENT_DATE_EMPTY"),
    ALREADY_PROPOSED_TRUCK(HttpStatus.BAD_REQUEST, 40011, "ALREADY_PROPOSED_TRUCK"),
    EVENT_TRUCK_NOT_FOUND(HttpStatus.NOT_FOUND, 40012, "EVENT_TRUCK_NOT_FOUND"),
    WRONG_EVENT_TRUCK_STATUS(HttpStatus.BAD_REQUEST, 40013, "WRONG_EVENT_TRUCK_STATUS"),
    WRONG_EVENT_RECRUITMENT_STATUS(HttpStatus.BAD_REQUEST, 40014, "WRONG_EVENT_RECRUITMENT_STATUS"),
    ALREADY_HANDLED_EVENT_APPLICATION(HttpStatus.BAD_REQUEST, 40015, "ALREADY_HANDLED_EVENT_APPLICATION"),
    ALREADY_HANDLED_EVENT_TRUCK(HttpStatus.BAD_REQUEST, 40016, "ALREADY_HANDLED_EVENT_TRUCK"),
    ALREADY_VIEWED_EVENT_NOTICE(HttpStatus.BAD_REQUEST, 40017, "ALREADY_VIEWED_EVENT_NOTICE");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    EventErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
