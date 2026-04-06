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
                .status(resolveStatus(eventRecruitDetail, endDate))
                .isRecruitEndOnSelection(eventRecruitDetail.getIsRecruitEndOnSelection())
                .applicantCount(eventRecruitDetail.getApplicantCount())
                .selectedCount(eventRecruitDetail.getSelectedCount())
                .recruitCount(eventRecruitDetail.getRecruitCount())
                .build();
    }

    public static String resolveStatus(EventRecruitDetail eventRecruitDetail, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        if ((endDate != null && endDate.isBefore(today))
                || (endDate == null && !eventRecruitDetail.isEventProgress())) {
            return "COMPLETED";
        }
        return eventRecruitDetail.getRecruitingStatus().toString();
    }
}
