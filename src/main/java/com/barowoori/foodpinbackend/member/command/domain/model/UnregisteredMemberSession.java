package com.barowoori.foodpinbackend.member.command.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UnregisteredMemberSession {
    private String sessionId;
    private String socialLoginId;
    private String refreshToken;
    private LocalDateTime registeredAt;
}
