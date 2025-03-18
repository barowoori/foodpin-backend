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

    //TODO EventCategoryDto, EventDocumentDto 유지 여부 의문
    // 어차피 컬럼 하나씩만 받는데 dto를 쓰는 게 맞는 지 모르겠습니다
    // 각 dto마다 toEntity 따로 사용 가능하고 리스트가 아닌 컬럼에다 example을 적어줄 수 있다는 장점이 있긴 한데 이게 과연 프론트한테도 괜찮은 구조일지 잘 모르겠습니다
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
}
