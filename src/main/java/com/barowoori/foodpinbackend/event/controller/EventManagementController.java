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

    @Operation(summary = "행사 지원자 선정/탈락 처리", description = "행사 등록자만 사용 가능" +
            "\n\n선정인 경우 isSelected = true, dates 포함 필수 / 탈락인 경우 isSelected = false")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "행사 등록자가 아닌 경우[40005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사 신청(EventApplication)을 못 찾을 경우[40009], " +
                    "지원 날짜가 없거나 행사 날짜와 일치하지 않는 경우[40009]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/v1/applications")
    public ResponseEntity<CommonResponse<String>> handleEventApplication(@Valid @RequestBody RequestEvent.HandleEventApplicationDto handleEventApplicationDto) {
        eventManagementService.handleEventApplication(handleEventApplicationDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event application handled successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 공지 생성", description = "행사 등록자만 사용 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "행사 등록자가 아닌 경우[40005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사를 못 찾을 경우[40000]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/v1/notice")
    public ResponseEntity<CommonResponse<String>> createEventNotice(@Valid @RequestBody RequestEvent.CreateEventNoticeDto createEventNoticeDto) {
        eventManagementService.createEventNotice(createEventNoticeDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event notice created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 공지 수정", description = "행사 등록자만 사용 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "행사 등록자가 아닌 경우[40005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사를 못 찾을 경우[40000], " +
                    "행사 공지를 못 찾을 경우[40008]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/v1/notice/{eventNoticeId}")
    public ResponseEntity<CommonResponse<String>> updateEventNotice(@Valid @PathVariable("eventNoticeId") String eventNoticeId,
                                                                    @Valid @RequestBody RequestEvent.UpdateEventNoticeDto updateEventNoticeDto) {
        eventManagementService.updateEventNotice(eventNoticeId, updateEventNoticeDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event notice updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 공지 삭제", description = "행사 등록자만 사용 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "행사 등록자가 아닌 경우[40005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사를 못 찾을 경우[40000], " +
                    "행사 공지를 못 찾을 경우[40008]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(value = "/v1/notice/{eventNoticeId}")
    public ResponseEntity<CommonResponse<String>> deleteEventNotice(@Valid @PathVariable("eventNoticeId") String eventNoticeId) {
        eventManagementService.deleteEventNotice(eventNoticeId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event notice deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
