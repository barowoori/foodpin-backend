package com.barowoori.foodpinbackend.event.command.application.dto;

import com.barowoori.foodpinbackend.event.command.domain.model.EventNotice;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

public class ResponseEvent {
    @Builder
    @Data
    @Getter
    public static class GetEventNoticeDto{
        private String id;
        private String title;
        private LocalDateTime createdAt;

        public static GetEventNoticeDto of(EventNotice eventNotice){
            return GetEventNoticeDto.builder()
                    .id(eventNotice.getId())
                    .title(eventNotice.getTitle())
                    .createdAt(eventNotice.getCreatedAt())
                    .build();
        }
    }
}
