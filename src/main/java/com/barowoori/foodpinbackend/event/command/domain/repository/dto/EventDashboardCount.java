package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventDashboardCount {
    private Long recruitingCount;
    private Long progressCount;
    private Long endCount;
}
