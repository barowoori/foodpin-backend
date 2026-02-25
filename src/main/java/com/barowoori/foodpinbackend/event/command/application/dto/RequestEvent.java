package com.barowoori.foodpinbackend.event.command.application.dto;

import com.barowoori.foodpinbackend.common.validation.UnicodeSize;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class RequestEvent {
    @Builder
    @Data
    @Getter
    public static class CreateEventDto {
        @NotNull
        @Valid
        private EventInfoDto eventInfoDto;

        @Valid
        @NotNull
        private EventRecruitDto eventRecruitDto;

        @Valid
        @NotNull
        private EventTargetDto eventTargetDto;

        @Valid
        @NotNull
        private EventDetailDto eventDetailDto;
    }

    @Getter
    public static class EventInfoDto {
        @UnicodeSize(min = 1, max = 30, message = "1자 이상 30자 이하로 입력하세요.")
        @Schema(description = "행사 이름")
        @NotEmpty
        private String name;

        @Schema(description = "행사 종류")
        @NotNull
        private EventType type;

        @Schema(description = "예상 참여 인원")
        @NotNull
        private ExpectedParticipants expectedParticipants;

        @Schema(description = "행사 사진 파일 id 리스트")
        private List<String> fileIdList;

        @Schema(description = "행사 지역 코드", example = "GU85")
        @NotEmpty
        private String regionCode;

        @NotEmpty
        @Valid
        private List<EventDateDto> eventDateDtoList;

        public Event toEntity(String creator) {
            return Event.builder()
                    .createdBy(creator)
                    .name(this.name)
                    .type(this.type)
                    .expectedParticipants(this.expectedParticipants)
                    .description(null)
                    .guidelines(null)
                    .contact(null)
                    .truckTypes(null)
                    .saleType(null)
                    .priceRange(null)
                    .cateringDetail(null)
                    .isDeleted(Boolean.FALSE)
                    .build();
        }
    }

    @Getter
    public static class EventRecruitDto {
        @Schema(description = "모집 마감 날짜, 미입력 시 행사 종료일로 설정")
        @Setter
        private LocalDateTime recruitEndDateTime;

        @Schema(description = "모집 인원")
        @NotNull
        private Integer recruitCount;

        @Schema(description = "일정 전체 참여 필수 여부")
        @NotNull
        private Boolean isFullAttendanceRequired;

        @Schema(description = "선정 시 모집 종료 여부")
        @NotNull
        private Boolean isRecruitEndOnSelection;

        public EventRecruitDetail toEntity(Event event, Boolean generatorRequirement,
                                           Boolean electricitySupportAvailability) {
            return EventRecruitDetail.builder()
                    .recruitEndDateTime(this.recruitEndDateTime.withSecond(0))
                    .recruitCount(this.recruitCount)
                    .applicantCount(0)
                    .selectedCount(0)
                    .event(event)
                    .generatorRequirement(generatorRequirement)
                    .electricitySupportAvailability(electricitySupportAvailability)
                    .isSelecting(Boolean.TRUE)
                    .recruitingStatus(EventRecruitingStatus.RECRUITING)
                    .isFullAttendanceRequired(this.isFullAttendanceRequired)
                    .isRecruitEndOnSelection(this.isRecruitEndOnSelection)
                    .build();
        }
    }

    @Getter
    public static class EventTargetDto {
        @NotNull
        @Schema(description = "모집 푸드트럭 유형")
        private Set<TruckType> truckTypes;

        @NotEmpty
        @Schema(description = "카테고리 코드 리스트")
        private List<String> eventCategoryCodeList;

        @Schema(description = "판매 유형")
        @NotNull
        private SaleType saleType;

        @Schema(description = "일반 판매 시 희망 가격대")
        private PriceRange priceRange;

        @UnicodeSize(max = 10000, message = "10,000자 이하로 입력하세요.")
        @Schema(description = "케이터링 세부 정보")
        private String cateringDetail;

        @JsonIgnore
        @AssertTrue(message = "일반판매는 희망 가격대가 필요하고, 케이터링은 세부 정보가 필요합니다.")
        public boolean isValidSaleTypeFields() {
            if (saleType == null) {
                return true;
            }
            if (saleType == SaleType.NORMAL) {
                return priceRange != null;
            }
            return cateringDetail != null && !cateringDetail.isBlank();
        }

    }

    @Getter
    public static class EventDetailDto {
        @UnicodeSize(min = 10, max = 10000, message = "10자 이상 10,000자 이하로 입력하세요.")
        @Schema(description = "행사 상세 정보")
        @NotEmpty
        private String description;

        @UnicodeSize(min = 10, max = 10000, message = "10자 이상 10,000자 이하로 입력하세요.")
        @Schema(description = "행사 유의 사항")
        @NotEmpty
        private String guidelines;

        @Schema(description = "연락처")
        @NotEmpty
        private String contact;

        @Schema(description = "전기 지원 여부")
        @NotNull
        private Boolean electricitySupportAvailability;

        @Schema(description = "발전기 필요 여부")
        @NotNull
        private Boolean generatorRequirement;

    }

    @Getter
    public static class EventDateDto {
        @Schema(description = "행사 진행 일자", example = "2025-03-06")
        @NotNull
        private LocalDate date;

        @Schema(description = "시작 시간", example = "09:00:00")
        @NotNull
        private LocalTime startTime;

        @Schema(description = "종료 시간", example = "20:00:00")
        @NotNull
        private LocalTime endTime;

        public EventDate toEntity(Event event) {
            return EventDate.builder()
                    .date(this.date)
                    .startTime(this.startTime)
                    .endTime(this.endTime)
                    .event(event)
                    .build();
        }
    }

    @Getter
    public static class UpdateEventInfoDto {
        @UnicodeSize(min = 1, max = 30, message = "1자 이상 30자 이하로 입력하세요.")
        @Schema(description = "행사 이름")
        @NotEmpty
        private String name;

        @Schema(description = "행사 종류")
        @NotNull
        private EventType type;

        @Schema(description = "예상 참여 인원")
        @NotNull
        private ExpectedParticipants expectedParticipants;

        @Schema(description = "행사 사진 파일 id 리스트")
        private List<String> fileIdList;

        @NotEmpty
        private List<EventDateDto> eventDateDtoList;

        @Schema(description = "행사 지역 코드", example = "GU85")
        @NotEmpty
        private String regionCode;
    }

    @Getter
    public static class UpdateEventRecruitDto {
        @Schema(description = "모집 마감 날짜, 미입력 시 행사 종료일로 설정")
        @Setter
        private LocalDateTime recruitEndDateTime;

        @Schema(description = "모집 인원")
        @NotNull
        private Integer recruitCount;

        @Schema(description = "일정 전체 참여 필수 여부")
        @NotNull
        private Boolean isFullAttendanceRequired;

        @Schema(description = "선정 시 모집 종료 여부")
        @NotNull
        private Boolean isRecruitEndOnSelection;
    }

    @Getter
    public static class UpdateEventTargetDto {
        @NotNull
        @Schema(description = "모집 푸드트럭 유형")
        private Set<TruckType> truckTypes;

        @NotEmpty
        @Schema(description = "카테고리 코드 리스트")
        private List<String> eventCategoryCodeList;

        @Schema(description = "판매 유형")
        @NotNull
        private SaleType saleType;

        @Schema(description = "일반 판매 시 희망 가격대")
        private PriceRange priceRange;

        @UnicodeSize(max = 10000, message = "10,000자 이하로 입력하세요.")
        @Schema(description = "케이터링 세부 정보")
        private String cateringDetail;

        @JsonIgnore
        @AssertTrue(message = "일반판매는 희망 가격대가 필요하고, 케이터링은 세부 정보가 필요합니다.")
        public boolean isValidSaleTypeFields() {
            if (saleType == null) {
                return true;
            }
            if (saleType == SaleType.NORMAL) {
                return priceRange != null;
            }
            return cateringDetail != null && !cateringDetail.isBlank();
        }

    }

    @Getter
    public static class UpdateEventDetailDto {
        @UnicodeSize(min = 10, max = 10000, message = "10자 이상 10,000자 이하로 입력하세요.")
        @Schema(description = "행사 상세 정보")
        @NotEmpty
        private String description;

        @UnicodeSize(min = 10, max = 10000, message = "10자 이상 10,000자 이하로 입력하세요.")
        @Schema(description = "행사 유의 사항")
        @NotEmpty
        private String guidelines;

        @Schema(description = "연락처")
        @NotEmpty
        private String contact;

        @Schema(description = "전기 지원 여부")
        @NotNull
        private Boolean electricitySupportAvailability;

        @Schema(description = "발전기 필요 여부")
        @NotNull
        private Boolean generatorRequirement;

    }

    @Getter
    public static class UpdateEventDocumentDto {
        @Schema(description = "제출 서류 종류 리스트, 만약 없음이면 null")
        private List<DocumentType> eventDocumentTypeList;

        @Schema(description = "서류 제출 이메일")
        @NotEmpty
        private String submissionEmail;

        @Schema(description = "서류 제출 대상(지원자 전체 / 선정자만)")
        @NotNull
        private EventDocumentSubmissionTarget documentSubmissionTarget;
    }

    @Builder
    @Data
    public static class ProposeEventDto {
        @Schema(description = "제안할 행사 id")
        @NotEmpty
        private String eventId;

        @Schema(description = "제안할 트럭 id")
        @NotEmpty
        private String truckId;

        public EventProposal toEntity(Event event, Truck truck) {
            return EventProposal.builder()
                    .event(event)
                    .truck(truck)
                    .build();
        }
    }

    @Builder
    @Data
    @Getter
    public static class ApplyEventDto {
        @Schema(description = "지원할 행사 id")
        @NotEmpty
        private String eventId;

        @Schema(description = "지원할 트럭 id")
        @NotEmpty
        private String truckId;

        @Schema(description = "지원할 날짜(EventDate) id 리스트")
        @NotEmpty
        private List<String> eventDateIdList;

        public EventApplication toEntity(Truck truck, Event event) {
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
    public static class HandleEventApplicationDto {
        @Schema(description = "선정 여부")
        @NotNull
        private Boolean isSelected;

        @Schema(description = "선정할 날짜(EventDate) id 리스트, 선정인 경우 필수")
        private List<String> eventDateIdList;
    }

    @Builder
    @Getter
    public static class HandleEventRecruitmentDto {
        @Schema(description = "행사 id")
        @NotEmpty
        private String eventId;

        @Schema(description = "모집 종료/취소 여부")
        @NotNull
        private EventRecruitingStatus recruitmentStatus;
    }

    @Builder
    @Getter
    public static class HandleEventTruckDto {
        @Schema(description = "EventTruck id")
        @NotEmpty
        private String eventTruckId;

        @Schema(description = "행사 참여 여부")
        @NotNull
        private EventTruckStatus eventTruckStatus;
    }

    @Builder
    @Getter
    public static class CreateEventNoticeDto {
        @Schema(description = "행사 id")
        @NotEmpty
        private String eventId;

        @Schema(description = "제목")
        @NotEmpty
        private String title;

        @Schema(description = "내용")
        @NotEmpty
        private String content;

        public EventNotice toEntity(Event event) {
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
    public static class UpdateEventNoticeDto {
        @Schema(description = "제목")
        @NotEmpty
        private String title;

        @Schema(description = "내용")
        @NotEmpty
        private String content;
    }
}
