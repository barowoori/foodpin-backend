package com.barowoori.foodpinbackend.member.command.domain.exception;

import com.barowoori.foodpinbackend.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NICKNAME_EMPTY(HttpStatus.BAD_REQUEST, 20000, "MEMBER_NICKNAME_EMPTY"),
    MEMBER_PHONE_EMPTY(HttpStatus.BAD_REQUEST, 20001, "MEMBER_PHONE_EMPTY"),
    MEMBER_ORIGIN_REFRESH_TOKEN_EMPTY(HttpStatus.BAD_REQUEST, 20002, "MEMBER_ORIGIN_REFRESH_TOKEN_EMPTY"),
    MEMBER_SOCIAL_LOGIN_INFO_EXISTS(HttpStatus.BAD_REQUEST, 20003, "MEMBER_SOCIAL_LOGIN_INFO_EXISTS"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, 20004, "MEMBER_NOT_FOUND");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    MemberErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
