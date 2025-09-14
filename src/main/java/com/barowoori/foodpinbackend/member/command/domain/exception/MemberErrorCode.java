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
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, 20004, "MEMBER_NOT_FOUND"),
    REFRESH_TOKEN_MATCH_FAILED(HttpStatus.UNAUTHORIZED, 20005, "REFRESH_TOKEN_MATCH_FAILED"),
    MEMBER_PROFILE_NOT_FOUND(HttpStatus.UNAUTHORIZED, 20006, "MEMBER_PROFILE_NOT_FOUND"),
    ONLY_UNREGISTERED_ALLOWED(HttpStatus.BAD_REQUEST, 20007, "ONLY_UNREGISTERED_ALLOWED"),
    AUTH_CODE_EXPIRED(HttpStatus.BAD_REQUEST, 20008, "AUTH_CODE_EXPIRED"),
    MEMBER_SOCIAL_TYPE_EMPTY(HttpStatus.BAD_REQUEST, 20009, "MEMBER_SOCIAL_TYPE_EMPTY"),
    INVALID_IDENTITY_TOKEN(HttpStatus.BAD_REQUEST, 20010, "INVALID_IDENTITY_TOKEN"),
    AUTH_CODE_EMPTY(HttpStatus.BAD_REQUEST, 20011, "AUTH_CODE_EMPTY"),
    MEMBER_PHONE_EXISTS(HttpStatus.BAD_REQUEST, 20012, "MEMBER_PHONE_EXISTS"),
    MEMBER_NICKNAME_EXISTS(HttpStatus.BAD_REQUEST, 20013, "MEMBER_NICKNAME_EXISTS");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    MemberErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
