package com.barowoori.foodpinbackend.behaviorLog.application;

import com.amazonaws.services.sqs.AmazonSQS;
import com.barowoori.foodpinbackend.behaviorLog.application.dto.BehaviorLogMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 비회원 행동 로그를 외부(SQS → Lambda → DynamoDB)로 보내는 공통 발행 코드.
 * 비동기로 처리하며 발행 실패는 메인 플로우에 영향을 주지 않도록 삼킨다.
 * 트랜잭션 안에서는 BehaviorLogEventListener(AFTER_COMMIT)를 통해,
 * 트랜잭션 밖(향후 로그 직접 수집 API)에서는 이 메서드를 바로 호출해 재사용한다.
 */
@Component
@RequiredArgsConstructor
public class BehaviorLogPublisher {

    private final Logger LOGGER = LoggerFactory.getLogger(BehaviorLogPublisher.class);
    private final AmazonSQS amazonSQS;
    private final ObjectMapper objectMapper;

    @Value("${sqs.queue-url:}")
    private String queueUrl;

    @Async("behaviorLogExecutor")
    public void publish(BehaviorLogMessage message) {
        if (queueUrl == null || queueUrl.isBlank()) {
            LOGGER.warn("[behavior-log] sqs.queue-url 미설정 - 발행 생략 (event={})", message.getEvent());
            return;
        }
        try {
            amazonSQS.sendMessage(queueUrl, objectMapper.writeValueAsString(message));
        } catch (Exception e) {
            LOGGER.error("[behavior-log] SQS 발행 실패 (event={}): {}", message.getEvent(), e.getMessage());
        }
    }

    /**
     * 현재 요청 스레드에서 클라이언트 IP를 추출한다.
     * 비동기 발행 전에(요청 스레드에서) 호출해 message에 담아야 한다.
     */
    public static String resolveClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
