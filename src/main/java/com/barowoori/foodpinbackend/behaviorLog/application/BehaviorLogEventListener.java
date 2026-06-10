package com.barowoori.foodpinbackend.behaviorLog.application;

import com.barowoori.foodpinbackend.behaviorLog.application.event.BehaviorLogEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 트랜잭션 커밋 이후에만 행동 로그를 발행한다(가입 롤백 시 로그가 새 나가지 않도록).
 * 실제 SQS 전송은 BehaviorLogPublisher가 @Async로 처리해 메인 응답을 막지 않는다.
 */
@Component
@RequiredArgsConstructor
public class BehaviorLogEventListener {

    private final BehaviorLogPublisher behaviorLogPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BehaviorLogEvent event) {
        behaviorLogPublisher.publish(event.getMessage());
    }
}
