package com.barowoori.foodpinbackend.member.command.domain.repository;

import com.barowoori.foodpinbackend.member.command.domain.model.UnregisteredMemberSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UnregisteredMemberRedisRepository {

    private static final Duration RETENTION = Duration.ofDays(90);
    private static final String SESSION_KEY_PREFIX = "guest:session:";
    private static final String SOCIAL_KEY_PREFIX = "guest:social:";
    private static final String REGISTERED_ZSET_KEY = "guest:registered";
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    private final StringRedisTemplate stringRedisTemplate;

    public UnregisteredMemberSession createSession(String socialLoginId) {
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime registeredAt = LocalDateTime.now(ZONE_ID);
        String sessionKey = sessionKey(sessionId);

        stringRedisTemplate.opsForHash().putAll(sessionKey, Map.of(
                "sessionId", sessionId,
                "socialLoginId", socialLoginId,
                "registeredAt", registeredAt.toString(),
                "refreshToken", ""
        ));
        stringRedisTemplate.expire(sessionKey, RETENTION);
        stringRedisTemplate.opsForValue().set(socialKey(socialLoginId), sessionId, RETENTION);
        stringRedisTemplate.opsForZSet().add(REGISTERED_ZSET_KEY, sessionId, toEpochMilli(registeredAt));

        return UnregisteredMemberSession.builder()
                .sessionId(sessionId)
                .socialLoginId(socialLoginId)
                .registeredAt(registeredAt)
                .refreshToken("")
                .build();
    }

    public boolean existsBySocialLoginId(String socialLoginId) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(socialKey(socialLoginId)));
    }

    public String findSessionIdBySocialLoginId(String socialLoginId) {
        return stringRedisTemplate.opsForValue().get(socialKey(socialLoginId));
    }

    public UnregisteredMemberSession findBySessionId(String sessionId) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(sessionKey(sessionId));
        if (entries == null || entries.isEmpty()) {
            return null;
        }

        String registeredAt = (String) entries.get("registeredAt");
        return UnregisteredMemberSession.builder()
                .sessionId((String) entries.get("sessionId"))
                .socialLoginId((String) entries.get("socialLoginId"))
                .refreshToken((String) entries.get("refreshToken"))
                .registeredAt(registeredAt != null ? LocalDateTime.parse(registeredAt) : null)
                .build();
    }

    public void updateRefreshToken(String sessionId, String refreshToken) {
        String key = sessionKey(sessionId);
        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            return;
        }
        stringRedisTemplate.opsForHash().put(key, "refreshToken", refreshToken == null ? "" : refreshToken);
        stringRedisTemplate.expire(key, RETENTION);
    }

    public long countRegisteredBetween(LocalDateTime from, LocalDateTime to) {
        Long count = stringRedisTemplate.opsForZSet().count(
                REGISTERED_ZSET_KEY,
                toEpochMilli(from),
                toEpochMilli(to)
        );
        return count == null ? 0L : count;
    }

    private String sessionKey(String sessionId) {
        return SESSION_KEY_PREFIX + sessionId;
    }

    private String socialKey(String socialLoginId) {
        return SOCIAL_KEY_PREFIX + socialLoginId;
    }

    private double toEpochMilli(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZONE_ID).toInstant().toEpochMilli();
    }
}
