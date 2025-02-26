package com.barowoori.foodpinbackend.common.exception;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Builder
@Getter
public class ErrorResponse {
    @Schema(description = "코드", example = "10100")
    private Integer code;
    @Schema(description = "내용", example = "AUTHENTICATION_FAILED")
    private String message;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode, String extraMessage) {
        if (extraMessage == null) {
            return ResponseEntity
                    .status(errorCode.getHttpStatus())
                    .body(ErrorResponse.builder()
                            .code(errorCode.getCode())
                            .message(errorCode.getMessage())
                            .build());
        }
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .code(errorCode.getCode())
                        .message(extraMessage)
                        .build());
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(HttpStatus status, Integer code, String message) {
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.builder()
                        .code(code)
                        .message(message)
                        .build());
    }
}
