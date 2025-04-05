package com.barowoori.foodpinbackend.event.command.application.dto;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionInfo;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class RequestEvent {
    @Builder
    @Data
    @Getter
    public static class CreateEventDto{
        @NotEmpty
        private EventInfoDto eventInfoDto;
        @Schema(description = "행사 지역 코드", example = "GU85")
        @NotEmpty
        private String regionCode;
        @NotEmpty
        private EventRecruitDto eventRecruitDto;
        @NotEmpty
        private List<EventDateDto> eventDateDtoList;
        @NotEmpty
        @Schema(description = "카테고리 코드 리스트")
        private List<String> eventCategoryCodeList;
        @Schema(description = "제출 서류 종류 리스트, 만약 없음이면 null")
        private List<DocumentType> eventDocumentTypeList;
    }

    @Getter
    public static class EventInfoDto{
        @Schema(description = "행사 이름")
        @NotEmpty
        private String name;
        @Schema(description = "행사 상세 정보")
        @NotEmpty
        private String description;
        @Schema(description = "행사 유의 사항")
        @NotEmpty
        private String guidelines;
        @Schema(description = "서류 제출 이메일")
        @NotEmpty
        private String submissionEmail;
        @Schema(description = "서류 제출 대상(지원자 전체 / 선정자만)")
        @NotEmpty
        private EventDocumentSubmissionTarget documentSubmissionTarget;
        @Schema(description = "행사 사진 파일 id 리스트")
        private List<String> fileIdList;

        public Event toEntity(String creator){
            return Event.builder()
                    .createdBy(creator)
                    .name(this.name)
                    .description(this.description)
                    .guidelines(this.guidelines)
                    .documentSubmissionTarget(this.documentSubmissionTarget)
                    .submissionEmail(this.submissionEmail)
                    .status(EventStatus.RECRUITING)
                    .isDeleted(Boolean.FALSE)
                    .build();
        }
    }

    @Getter
    public static class EventRecruitDto{
        @Schema(description = "모집 마감 날짜")
        @NotEmpty
        private LocalDateTime recruitEndDateTime;
        @Schema(description = "모집 인원")
        @NotEmpty
        private Integer recruitCount;
        @Schema(description = "발전기 필요 여부")
        @NotEmpty
        private Boolean generatorRequirement;
        @Schema(description = "전기 지원 여부")
        @NotEmpty
        private Boolean electricitySupportAvailability;
        @Schema(description = "입점비")
        @NotEmpty
        private Integer entryFee;

        public EventRecruitDetail toEntity(Event event){
            return EventRecruitDetail.builder()
                    .recruitEndDateTime(this.recruitEndDateTime)
                    .recruitCount(this.recruitCount)
                    .applicantCount(0)
                    .selectedCount(0)
                    .event(event)
                    .generatorRequirement(this.generatorRequirement)
                    .electricitySupportAvailability(this.electricitySupportAvailability)
                    .entryFee(this.entryFee)
                    .build();
        }
    }

    @Getter
    public static class EventDateDto{
        @Schema(description = "행사 진행 일자", example = "2025-03-06")
        @NotEmpty
        private LocalDate date;
        @Schema(description = "시작 시간", example = "09:00:00")
        @NotEmpty
        private LocalTime startTime;
        @Schema(description = "종료 시간", example = "20:00:00")
        @NotEmpty
        private LocalTime endTime;

        public EventDate toEntity(Event event){
            return EventDate.builder()
                    .date(this.date)
                    .startTime(this.startTime)
                    .endTime(this.endTime)
                    .event(event)
                    .build();
        }
    }

    @Getter
    public static class UpdateEventInfoDto{
        @Schema(description = "행사 이름")
        @NotEmpty
        private String name;
        @Schema(description = "행사 사진 파일 id 리스트")
        private List<String> fileIdList;
        @NotEmpty
        private List<EventDateDto> eventDateDtoList;
        @Schema(description = "행사 지역 코드", example = "GU85")
        @NotEmpty
        private String regionCode;
    }

    @Getter
    public static class UpdateEventDetailDto{
        @NotEmpty
        @Schema(description = "카테고리 코드 리스트")
        private List<String> eventCategoryCodeList;
        @Schema(description = "행사 상세 정보")
        @NotEmpty
        private String description;
        @Schema(description = "행사 유의 사항")
        @NotEmpty
        private String guidelines;
    }

    @Getter
    public static class UpdateEventDocumentDto{
        @Schema(description = "제출 서류 종류 리스트, 만약 없음이면 null")
        private List<DocumentType> eventDocumentTypeList;
        @Schema(description = "서류 제출 이메일")
        @NotEmpty
        private String submissionEmail;
        @Schema(description = "서류 제출 대상(지원자 전체 / 선정자만)")
        @NotEmpty
        private EventDocumentSubmissionTarget documentSubmissionTarget;
    }

    @Builder
    @Data
    public static class ProposeEventDto{
        @Schema(description = "제안할 행사 id")
        @NotEmpty
        private String eventId;
        @Schema(description = "제안할 트럭 id")
        @NotEmpty
        private String truckId;

        public EventProposal toEntity(Event event, Truck truck){
            return EventProposal.builder()
                    .event(event)
                    .truck(truck)
                    .build();
        }
    }

    @Builder
    @Data
    @Getter
    public static class ApplyEventDto{
        @Schema(description = "지원할 행사 id")
        @NotEmpty
        private String eventId;
        @Schema(description = "지원할 트럭 id")
        @NotEmpty
        private String truckId;
        @Schema(description = "지원할 날짜(EventDate) id 리스트")
        @NotEmpty
        private List<String> eventDateIdList;

        //TODO EventApplication 기본값 설정 코드 정상 작동 시 아래서 status, isRead 설정 제거
        public EventApplication toEntity(Truck truck, Event event){
            return EventApplication.builder()
                    .truck(truck)
                    .event(event)
                    .status(EventApplicationStatus.PENDING)
                    .isRead(Boolean.FALSE)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class HandleEventApplicationDto{
        @Schema(description = "선정 여부")
        @NotEmpty
        private Boolean isSelected;
        @Schema(description = "선정할 날짜(EventDate) id 리스트, 선정인 경우 필수")
        private List<String> eventDateIdList;
    }

    @Builder
    @Getter
    public static class HandleEventRecruitmentDto{
        @Schema(description = "행사 id")
        @NotEmpty
        private String eventId;
        @Schema(description = "모집 종료/취소 여부")
        @NotEmpty
        private EventStatus recruitmentStatus;
    }

    @Builder
    @Getter
    public static class HandleEventTruckDto{
        @Schema(description = "EventTruck id")
        @NotEmpty
        private String eventTruckId;
        @Schema(description = "행사 참여 여부")
        @NotEmpty
        private EventTruckStatus eventTruckStatus;
    }

    @Builder
    @Getter
    public static class CreateEventNoticeDto{
        @Schema(description = "행사 id")
        @NotEmpty
        private String eventId;
        @Schema(description = "제목")
        @NotEmpty
        private String title;
        @Schema(description = "내용")
        @NotEmpty
        private String content;

        public EventNotice toEntity(Event event){
            return EventNotice.builder()
                    .title(this.title)
                    .content(this.content)
                    .isDeleted(Boolean.FALSE)
                    .event(event)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class UpdateEventNoticeDto{
        @Schema(description = "제목")
        @NotEmpty
        private String title;
        @Schema(description = "내용")
        @NotEmpty
        private String content;
    }
}
