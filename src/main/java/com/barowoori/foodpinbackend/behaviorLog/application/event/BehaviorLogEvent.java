package com.barowoori.foodpinbackend.behaviorLog.application.event;

import com.barowoori.foodpinbackend.behaviorLog.application.dto.BehaviorLogMessage;
import lombok.Getter;

/**
 * 트랜잭션 안에서 발행되어 커밋 이후 SQS로 전송되는 행동 로그 이벤트.
 * ip·sessionId 등 요청 컨텍스트 값은 발행 시점(요청 스레드)에 미리 message에 담는다.
 */
@Getter
public class BehaviorLogEvent {
    private final BehaviorLogMessage message;

    public BehaviorLogEvent(BehaviorLogMessage message) {
        this.message = message;
    }
}
