package com.barowoori.foodpinbackend.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Getter
@Builder
public class CommonResponse<T> {
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    @Builder.Default
    private LocalDateTime createAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    private T data;
}
