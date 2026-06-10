package com.barowoori.foodpinbackend.behaviorLog.application.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * SQS로 발행되는 비회원 행동 로그 공통 메시지.
 * clientTimestamp는 프론트 직접 호출 케이스에만, memberId는 UNREG_SIGNUP에만 존재한다.
 */
@Getter
@Builder
public class BehaviorLogMessage {
    private final String event;
    private final String sessionId;
    private final String memberId;
    private final String clientTimestamp;
    private final String serverReceivedAt;
    private final String ip;
}
