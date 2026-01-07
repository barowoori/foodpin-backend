package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRecruitDetail;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRecruitingStatus;
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

    public static RecruitInfo of(EventRecruitDetail eventRecruitDetail) {
        return RecruitInfo.builder()
                .status(convertStatus(eventRecruitDetail))
                .isRecruitEndOnSelection(eventRecruitDetail.getIsRecruitEndOnSelection())
                .applicantCount(eventRecruitDetail.getApplicantCount())
                .selectedCount(eventRecruitDetail.getSelectedCount())
                .recruitCount(eventRecruitDetail.getRecruitCount())
                .build();
    }

    private static String convertStatus(EventRecruitDetail eventRecruitDetail) {
        if (eventRecruitDetail.getRecruitingStatus().equals(EventRecruitingStatus.RECRUITMENT_CANCELLED)){
            return eventRecruitDetail.getRecruitingStatus().toString();
        }
        if (!eventRecruitDetail.isEventProgress()) {
            return "COMPLETED";
        }
        return eventRecruitDetail.getRecruitingStatus().toString();
    }
}
