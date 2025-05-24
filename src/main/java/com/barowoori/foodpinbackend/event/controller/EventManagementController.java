package com.barowoori.foodpinbackend.event.controller;

import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.common.exception.ErrorResponse;
import com.barowoori.foodpinbackend.event.command.application.dto.RequestEvent;
import com.barowoori.foodpinbackend.event.command.application.service.EventManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "행사 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/events/management")
public class EventManagementController {
    private final EventManagementService eventManagementService;

    @Operation(summary = "행사 지원자 선정/탈락 처리", description = "행사 주최자만 사용 가능" +
            "\n\n선정인 경우 isSelected = true, dates 포함 필수 / 탈락인 경우 isSelected = false")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "행사 주최자가 아닌 경우[40005], " +
                    "선정할 날짜가 누락된 경우[40010], 이미 처리(선정/탈락)한 경우[40015]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사 신청(EventApplication)을 못 찾을 경우[40009], " +
                    "선정할 날짜가 행사 날짜와 일치하지 않는 경우[40006]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/v1/applications/{eventApplicationId}/pending")
    public ResponseEntity<CommonResponse<String>> handleEventApplication(@Valid @PathVariable("eventApplicationId") String eventApplicationId,
                                                                         @RequestBody RequestEvent.HandleEventApplicationDto handleEventApplicationDto) {
        eventManagementService.handleEventApplication(eventApplicationId, handleEventApplicationDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event application handled successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 모집 마감/취소 처리", description = "행사 주최자만 사용 가능" +
            "\n\n마감인 경우 recruitmentStatus = RECRUITMENT_CLOSED, 취소인 경우 recruitmentStatus = RECRUITMENT_CANCELLED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "행사 주최자가 아닌 경우[40005], " +
                    "모집 종료/취소 여부가 잘못된 경우[40014]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사를 못 찾을 경우[40000]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/v1/recruitments")
    public ResponseEntity<CommonResponse<String>> handleEventRecruitment(@RequestBody RequestEvent.HandleEventRecruitmentDto handleEventRecruitmentDto) {
        eventManagementService.handleEventRecruitment(handleEventRecruitmentDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event recruitment handled successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 선정 수동 마감", description = "행사 선정 마감 수동 처리. 마감 시 모집이 종료되지 않았다면 자동 종료, PENDING 상태인 EventApplication은 모두 REJECTED 처리.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "행사 주최자가 아닌 경우[40005], 이미 마감된 경우[40018]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사를 못 찾을 경우[40000]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/v1/selections/{eventId}/close")
    public ResponseEntity<CommonResponse<String>> closeEventSelection(@PathVariable("eventId") String eventId) {
        eventManagementService.closeEventSelection(eventId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event selection closed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "지원자 선정 취소", description = "트럭 선정 취소. 이미 회신(CONFIRMED)한 경우 취소 불가.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "행사 주최자가 아닌 경우[40005], 선정된 상태가 아닌 경우[40019], 이미 참여 회신(CONFIRMED)된 경우[40020]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "지원 정보를 못 찾을 경우[40009], 트럭 정보를 못 찾을 경우[40012]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/v1/selections/{eventApplicationId}/cancel")
    public ResponseEntity<CommonResponse<String>> cancelEventSelection(@PathVariable("eventApplicationId") String eventApplicationId) {
        eventManagementService.cancelEventSelection(eventApplicationId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event selection canceled successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 신청 읽음 처리", description = "행사 주최자가 호출 시에만 읽음 처리 적용")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사 신청(EventApplication)을 못 찾을 경우[40009]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/v1/applications/{eventApplicationId}/pending")
    public ResponseEntity<CommonResponse<String>> readEventApplication(@Valid @PathVariable("eventApplicationId") String eventApplicationId) {
        eventManagementService.readEventApplication(eventApplicationId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event application read successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 공지 생성", description = "행사 주최자만 사용 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "행사 주최자가 아닌 경우[40005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사를 못 찾을 경우[40000]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/v1/notices")
    public ResponseEntity<CommonResponse<String>> createEventNotice(@Valid @RequestBody RequestEvent.CreateEventNoticeDto createEventNoticeDto) {
        eventManagementService.createEventNotice(createEventNoticeDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event notice created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 공지 수정", description = "행사 주최자만 사용 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "행사 주최자가 아닌 경우[40005], " +
                    "이미 조회된 공지인 경우[40017]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사를 못 찾을 경우[40000], " +
                    "행사 공지를 못 찾을 경우[40008]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/v1/notices/{eventNoticeId}")
    public ResponseEntity<CommonResponse<String>> updateEventNotice(@Valid @PathVariable("eventNoticeId") String eventNoticeId,
                                                                    @Valid @RequestBody RequestEvent.UpdateEventNoticeDto updateEventNoticeDto) {
        eventManagementService.updateEventNotice(eventNoticeId, updateEventNoticeDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event notice updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 공지 삭제", description = "행사 주최자만 사용 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "행사 주최자가 아닌 경우[40005], " +
                    "이미 조회된 공지인 경우[40017]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사를 못 찾을 경우[40000], " +
                    "행사 공지를 못 찾을 경우[40008]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(value = "/v1/notices/{eventNoticeId}")
    public ResponseEntity<CommonResponse<String>> deleteEventNotice(@Valid @PathVariable("eventNoticeId") String eventNoticeId) {
        eventManagementService.deleteEventNotice(eventNoticeId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event notice deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
