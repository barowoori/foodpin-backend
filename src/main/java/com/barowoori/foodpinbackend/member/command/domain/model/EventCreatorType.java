package com.barowoori.foodpinbackend.member.command.domain.model;

import lombok.Getter;

@Getter
public enum EventCreatorType {
    USER,      // 일반 사용자
    ADMIN      // 관리자
}
