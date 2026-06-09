package com.barowoori.foodpinbackend.behaviorLog.application.dto;

import com.barowoori.foodpinbackend.behaviorLog.domain.model.BehaviorEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RequestBehaviorLog {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollectDto {
        @Schema(description = "이벤트 종류", example = "UNREG_HOME_NOTIFICATION")
        @NotNull(message = "event는 필수입니다")
        private BehaviorEvent event;
        @Schema(description = "클라이언트 시각(ISO 8601)", example = "2026-06-07T10:00:00.000Z")
        private String timestamp;
    }
}
