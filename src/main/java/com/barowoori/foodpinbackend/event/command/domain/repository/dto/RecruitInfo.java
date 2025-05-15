package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRecruitDetail;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRecruitingStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecruitInfo {
    private EventRecruitingStatus status;
    private Integer applicantCount;
    private Integer selectedCount;
    private Integer recruitCount;

    public static RecruitInfo of(EventRecruitDetail eventRecruitDetail) {
        return RecruitInfo.builder()
                .status(eventRecruitDetail.getRecruitingStatus())
                .applicantCount(eventRecruitDetail.getApplicantCount())
                .selectedCount(eventRecruitDetail.getSelectedCount())
                .recruitCount(eventRecruitDetail.getRecruitCount())
                .build();
    }
}
