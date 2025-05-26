package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventTruckManagerFcmInfoDto {
    private String eventId;
    private String eventName;
    private String memberId;
    private String fcmToken;
}
