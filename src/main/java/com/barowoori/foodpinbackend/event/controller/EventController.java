package com.barowoori.foodpinbackend.event.controller;


import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.common.exception.ErrorResponse;
import com.barowoori.foodpinbackend.event.command.application.dto.RequestEvent;
import com.barowoori.foodpinbackend.event.command.application.service.EventService;
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

@Tag(name = "행사 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    @Operation(summary = "행사 생성", description = "사진의 경우 파일 저장 api로 업로드 후 반환된 파일 id 리스트로 전달")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사 사진 파일을 못 찾을 경우[40001]," +
                    "지역을 못 찾을 경우[40002], 카테고리를 못 찾을 경우[40003]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/v1")
    public ResponseEntity<CommonResponse<String>> createEvent(@Valid @RequestBody RequestEvent.CreateEventDto createEventDto) {
        eventService.createEvent(createEventDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 기본 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사를 못 찾을 경우[40000]" +
                    "행사 사진 파일을 못 찾을 경우[40001], 지역을 못 찾을 경우[40002]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/v1/{eventId}/info")
    public ResponseEntity<CommonResponse<String>> updateEventInfo(@Valid @PathVariable("eventId") String eventId,
                                                                  @RequestBody RequestEvent.UpdateEventInfoDto updateEventInfoDto) {
        eventService.updateEventInfo(eventId, updateEventInfoDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event info updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 모집 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사 모집 정보를 못 찾을 경우[40004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/v1/{eventId}/recruit")
    public ResponseEntity<CommonResponse<String>> updateEventRecruit(@Valid @PathVariable("eventId") String eventId,
                                                                     @RequestBody RequestEvent.UpdateEventRecruitDto updateEventRecruitDto) {
        eventService.updateEventRecruit(eventId, updateEventRecruitDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event recruit info updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 상세 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "지역을 못 찾을 경우[40002]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/v1/{eventId}/detail")
    public ResponseEntity<CommonResponse<String>> updateEventDetail(@Valid @PathVariable("eventId") String eventId,
                                                                    @RequestBody RequestEvent.UpdateEventDetailDto updateEventDetailDto) {
        eventService.updateEventDetail(eventId, updateEventDetailDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event detail info updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 서류 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/v1/{eventId}/document")
    public ResponseEntity<CommonResponse<String>> updateEventDocument(@Valid @PathVariable("eventId") String eventId,
                                                                      @RequestBody RequestEvent.UpdateEventDocumentDto updateEventDocumentDto) {
        eventService.updateEventDocument(eventId, updateEventDocumentDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event document info updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
