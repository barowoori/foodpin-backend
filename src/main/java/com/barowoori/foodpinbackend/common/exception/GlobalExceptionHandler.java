package com.barowoori.foodpinbackend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        System.out.println("Custom exception occurred. Code : " + e.getErrorCode() + ". Message : " + e.getExtraMessage());
        return ErrorResponse.toResponseEntity(e.getErrorCode(), e.getExtraMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        // 유효성 검사 실패에 대한 처리 로직을 구현
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String defaultMessage = "";
        for (FieldError fieldError : fieldErrors) {
            defaultMessage = fieldError.getDefaultMessage();
        }
        return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, 10000, defaultMessage);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        // 예외를 로깅하거나 다른 처리를 수행할 수 있습니다.
        ex.printStackTrace();

        // 클라이언트에 반환할 오류 메시지를 작성합니다.
        String errorMessage = "지원하지 않는 미디어 타입입니다 " + ex.getContentType();
        return ErrorResponse.toResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE, 10001, errorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        // 예외를 로깅하거나 다른 처리를 수행할 수 있습니다.
        ex.printStackTrace();

        // 클라이언트에 반환할 오류 메시지를 작성합니다.
        String errorMessage = "HTTP 요청 메시지가 잘못되었습니다 " + ex.getMostSpecificCause().getMessage();
        return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, 10002, errorMessage);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestPart(MissingServletRequestPartException ex) {
        // 예외를 로깅하거나 다른 처리를 수행할 수 있습니다.
        ex.printStackTrace();

        // 클라이언트에 반환할 오류 메시지를 작성합니다.
        String errorMessage = ex.getRequestPartName() + " HTTP 요청 파트가 누락되었습니다" ;
        return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, 10003, errorMessage);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        // 예외를 로깅하거나 다른 처리를 수행할 수 있습니다.
        ex.printStackTrace();

        // 클라이언트에 반환할 오류 메시지를 작성합니다.
        String errorMessage = "지원하지 않는 HTTP 메서드입니다. 지원되는 메서드: " + String.join(", ", ex.getSupportedMethods());
        return ErrorResponse.toResponseEntity(HttpStatus.METHOD_NOT_ALLOWED, 10004, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        String errorMessage = "Internal Server Error: " + ex.getMessage();
        return ErrorResponse.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, 10004, errorMessage);
    }
}
