package com.barowoori.foodpinbackend.config.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class RestControllerLoggingAspect {

    private final ObjectMapper mapper;

    public RestControllerLoggingAspect(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    // 모든 @RestController 메서드 실행 전에 로그
    @Before("within(@org.springframework.web.bind.annotation.RestController *)")
    public void logBeforeController(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        String argsStr = Arrays.stream(joinPoint.getArgs())
                .map(this::safeToLog)
                .collect(Collectors.joining(", "));

        log.info("[{}] {} API called. args=[{}]", className, methodName, argsStr);
    }

    // 예외 발생 시 로깅
    @AfterThrowing(pointcut = "within(@org.springframework.web.bind.annotation.RestController *)", throwing = "ex")
    public void logControllerException(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.error("[{}] {} API threw exception: {}", className, methodName, ex.getMessage(), ex);
    }

    // 안전하게 직렬화 (민감한 타입은 제외)
    private String safeToLog(Object arg) {
        if (arg == null) return "null";
        if (arg instanceof HttpServletRequest
                || arg instanceof HttpServletResponse
                || arg instanceof MultipartFile) {
            return arg.getClass().getSimpleName();
        }
        try {
            return mapper.writeValueAsString(arg);
        } catch (Exception e) {
            return String.valueOf(arg);
        }
    }
}

