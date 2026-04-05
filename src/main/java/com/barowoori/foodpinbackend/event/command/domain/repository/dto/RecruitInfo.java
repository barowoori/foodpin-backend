package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRecruitDetail;
import com.barowoori.foodpinbackend.event.command.domain.service.EventDateCalculator;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RecruitInfo {
    private String status;
    private Boolean isRecruitEndOnSelection;
    private Integer applicantCount;
    private Integer selectedCount;
    private Integer recruitCount;

    public static RecruitInfo of(Event event, EventRecruitDetail eventRecruitDetail) {
        return of(eventRecruitDetail, EventDateCalculator.getMaxDate(event));
    }

    public static RecruitInfo of(EventRecruitDetail eventRecruitDetail) {
        return of(eventRecruitDetail, null);
    }

    public static RecruitInfo of(EventRecruitDetail eventRecruitDetail, LocalDate endDate) {
        return RecruitInfo.builder()
                .status(convertStatus(eventRecruitDetail, endDate))
                .isRecruitEndOnSelection(eventRecruitDetail.getIsRecruitEndOnSelection())
                .applicantCount(eventRecruitDetail.getApplicantCount())
                .selectedCount(eventRecruitDetail.getSelectedCount())
                .recruitCount(eventRecruitDetail.getRecruitCount())
                .build();
    }

    private static String convertStatus(EventRecruitDetail eventRecruitDetail, LocalDate endDate) {
        if ((endDate != null && endDate.isBefore(LocalDate.now()))
                || (endDate == null && !eventRecruitDetail.isEventProgress())) {
            return "COMPLETED";
        }
        return eventRecruitDetail.getRecruitingStatus().toString();
    }
}
