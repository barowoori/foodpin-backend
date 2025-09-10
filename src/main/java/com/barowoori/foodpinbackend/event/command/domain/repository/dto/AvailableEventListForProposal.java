package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.service.EventDateCalculator;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AvailableEventListForProposal {
    private String id;
    private String name;
    private Boolean isProposal;
    private LocalDate recruitStartDate;
    private LocalDate recruitEndDate;
    private LocalDate startDate;
    private LocalDate endDate;

    public static AvailableEventListForProposal of(Event event, Boolean isProposal){
        return AvailableEventListForProposal.builder()
                .id(event.getId())
                .name(event.getName())
                .isProposal(isProposal)
                .recruitStartDate(event.getRecruitDetail().getCreatedAt().toLocalDate())
                .recruitEndDate(event.getRecruitDetail().getRecruitEndDateTime().toLocalDate())
                .startDate(EventDateCalculator.getMinDate(event))
                .endDate(EventDateCalculator.getMaxDate(event))
                .build();
    }

}
