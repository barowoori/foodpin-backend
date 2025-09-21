package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRecruitDetail;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRecruitingStatus;
import lombok.Builder;
import lombok.Getter;

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
                .status(convertStatus(eventRecruitDetail.getRecruitingStatus(), eventRecruitDetail.getIsSelecting()))
                .isRecruitEndOnSelection(eventRecruitDetail.getIsRecruitEndOnSelection())
                .applicantCount(eventRecruitDetail.getApplicantCount())
                .selectedCount(eventRecruitDetail.getSelectedCount())
                .recruitCount(eventRecruitDetail.getRecruitCount())
                .build();
    }
    private static String convertStatus(EventRecruitingStatus status, Boolean isSelecting){
        if (!isSelecting){
            return "COMPLETED";
        }
        return status.toString();
    }
}
