package com.barowoori.foodpinbackend.event.command.application.dto;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionInfo;
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
        @NotEmpty
        private EventRegionDto eventRegionDto;
        @NotEmpty
        private EventRecruitDto eventRecruitDto;
        @NotEmpty
        private List<EventDateDto> eventDateDtoList;
        @NotEmpty
        private List<EventCategoryDto> eventCategoryDtoList;
        @NotEmpty
        private List<EventDocumentDto> eventDocumentDtoList;
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
                    .status(EventStatus.IN_PROGRESS)
                    .build();
        }
    }

    @Getter
    public static class EventRegionDto{
        @Schema(description = "행사 지역 코드", example = "GU85")
        @NotEmpty
        private String regionCode;

        public EventRegion toEntity(Event event, RegionInfo regionInfo){
            return EventRegion.builder()
                    .regionType(regionInfo.getRegionType())
                    .regionId(regionInfo.getRegionId())
                    .event(event)
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
    public static class EventCategoryDto{
        @Schema(description = "카테고리 코드", example = "C01")
        @NotEmpty
        private String categoryCode;

        public EventCategory toEntity(Event event, Category category){
            return EventCategory.builder()
                    .event(event)
                    .category(category)
                    .build();
        }
    }

    @Getter
    public static class EventDocumentDto{
        @Schema(description = "서류 종류")
        @NotEmpty
        private DocumentType type;

        public EventDocument toEntity(Event event){
            return EventDocument.builder()
                    .event(event)
                    .type(this.type)
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
        @NotEmpty
        private EventRegionDto eventRegionDto;
    }

    @Getter
    public static class UpdateEventRecruitDto{
        @NotEmpty
        EventRecruitDto eventRecruitDto;
    }

    @Getter
    public static class UpdateEventDetailDto{
        @NotEmpty
        private List<EventCategoryDto> eventCategoryDtoList;
        @Schema(description = "행사 상세 정보")
        @NotEmpty
        private String description;
        @Schema(description = "행사 유의 사항")
        @NotEmpty
        private String guidelines;
    }

    @Getter
    public static class UpdateEventDocumentDto{
        @NotEmpty
        private List<EventDocumentDto> eventDocumentDtoList;
        @Schema(description = "서류 제출 이메일")
        @NotEmpty
        private String submissionEmail;
        @Schema(description = "서류 제출 대상(지원자 전체 / 선정자만)")
        @NotEmpty
        private EventDocumentSubmissionTarget documentSubmissionTarget;
    }
}
