package com.barowoori.foodpinbackend.behaviorLog.application;

import com.barowoori.foodpinbackend.behaviorLog.application.dto.BehaviorLogMessage;
import com.barowoori.foodpinbackend.behaviorLog.application.dto.RequestBehaviorLog;
import com.barowoori.foodpinbackend.member.command.domain.model.GuestMember;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * 프론트가 직접 호출하는 행동 로그 수집(API 없이 모달만 뜨는 케이스).
 * 트랜잭션이 없으므로 BehaviorLogPublisher를 바로 호출한다.
 * 식별 가능한 sessionId가 있을 때만 적재하며(없으면 분석 가치가 없어 무시), 실제 전송은 @Async로 처리한다.
 */
@Service
@RequiredArgsConstructor
public class BehaviorLogService {

    private final BehaviorLogPublisher behaviorLogPublisher;

    public void collect(RequestBehaviorLog.CollectDto dto) {
        String sessionId = currentGuestSessionId();
        if (sessionId == null) {
            return;
        }

        behaviorLogPublisher.publish(BehaviorLogMessage.builder()
                .event(dto.getEvent().name())
                .sessionId(sessionId)
                .clientTimestamp(dto.getTimestamp())
                .serverReceivedAt(Instant.now().toString())
                .ip(BehaviorLogPublisher.resolveClientIp())
                .build());
    }

    private String currentGuestSessionId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof GuestMember guestMember) {
            return guestMember.getSessionId();
        }
        return null;
    }
}
