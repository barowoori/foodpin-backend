package com.barowoori.foodpinbackend.event.controller;


import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.common.exception.ErrorResponse;
import com.barowoori.foodpinbackend.event.command.application.dto.RequestEvent;
import com.barowoori.foodpinbackend.event.command.application.service.EventService;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.*;
import com.barowoori.foodpinbackend.event.query.application.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDetail;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "행사 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private final EventDetailService eventDetailService;
    private final EventListService eventListService;
    private final EventManageListService eventManageListService;
    private final EventApplicationListService eventApplicationListService;
    private final TruckEventApplicationListService truckEventApplicationListService;
    private final EventApplicableTruckListService eventApplicableTruckListService;

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

    @Operation(summary = "행사 상세 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사를 못 찾을 경우[40000]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/v1/{eventId}/detail")
    public ResponseEntity<CommonResponse<EventDetail>> getEventDetail(@Valid @PathVariable("eventId") String eventId) {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        EventDetail eventDetail = eventDetailService.getEventDetail(memberId, eventId);
        CommonResponse<EventDetail> commonResponse = CommonResponse.<EventDetail>builder()
                .data(eventDetail)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 목록 조회", description = "정렬 : 최신순(createdAt, DESC), 조회순(views, DESC)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/v1")
    public ResponseEntity<CommonResponse<Page<EventList>>> getEventList(@RequestParam(value = "region", required = false) List<String> regionCodes,
                                                                        @RequestParam(value = "category", required = false) List<String> categoryCodes,
                                                                        @RequestParam(value = "search", required = false) String searchTerm,
                                                                        @RequestParam(value = "startDate", required = false) LocalDate startDate,
                                                                        @RequestParam(value = "endDate", required = false) LocalDate endDate,
                                                                        @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<EventList> eventLists = eventListService.findEventList(searchTerm, regionCodes, startDate, endDate, categoryCodes, pageable);
        CommonResponse<Page<EventList>> commonResponse = CommonResponse.<Page<EventList>>builder()
                .data(eventLists)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "찜한 행사 목록 조회", description = "정렬 : 최신순(createdAt, DESC), 조회순(views, DESC)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/v1/like")
    public ResponseEntity<CommonResponse<Page<EventList>>> getLikeEventList(@RequestParam(value = "region", required = false) List<String> regionCodes,
                                                                            @RequestParam(value = "category", required = false) List<String> categoryCodes,
                                                                            @RequestParam(value = "search", required = false) String searchTerm,
                                                                            @RequestParam(value = "startDate", required = false) LocalDate startDate,
                                                                            @RequestParam(value = "endDate", required = false) LocalDate endDate,
                                                                            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<EventList> eventLists = eventListService.findLikeEventList(memberId, searchTerm, regionCodes, startDate, endDate, categoryCodes, pageable);
        CommonResponse<Page<EventList>> commonResponse = CommonResponse.<Page<EventList>>builder()
                .data(eventLists)
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
                                                                     @RequestBody RequestEvent.EventRecruitDto eventRecruitDto) {
        eventService.updateEventRecruit(eventId, eventRecruitDto);
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

    @Operation(summary = "행사 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "행사 작성자가 아닐 경우[40005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(value = "/v1/{eventId}")
    public ResponseEntity<CommonResponse<String>> deleteEvent(@Valid @PathVariable("eventId") String eventId) {
        eventService.deleteEvent(eventId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 지원")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "이미 신청한 행사일 경우[40007]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "행사를 못 찾을 경우[40000]" +
                    "트럭을 못 찾을 경우[30000], 행사 날짜를 못 찾을 경우[40006]" +
                    "트럭 운영자가 아닐 경우[30004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/v1/applications")
    public ResponseEntity<CommonResponse<String>> applyEvent(@RequestBody RequestEvent.ApplyEventDto applyEventDto) {
        eventService.applyEvent(applyEventDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event applied successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "진행중인 공고 목록 조회", description = "status(상태) : ALL(전체), RECRUITING(모집중), SELECTING(선정중), IN_PROGRESS(진행중)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/v1/progress/status/{status}")
    public ResponseEntity<CommonResponse<Page<EventManageList>>> getProgressEventManageList(@PathVariable(value = "status") String status,
                                                                                            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<EventManageList> eventLists = eventManageListService.findProgressEventManageList(memberId, status, pageable);
        CommonResponse<Page<EventManageList>> commonResponse = CommonResponse.<Page<EventManageList>>builder()
                .data(eventLists)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "지난 공고 목록 조회", description = "status(상태) : ALL(전체), COMPLETED(종료), RECRUITMENT_CANCELLED(모집취소)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/v1/completed/status/{status}")
    public ResponseEntity<CommonResponse<Page<EventManageList>>> getCompletedEventManageList(@PathVariable(value = "status") String status,
                                                                                             @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<EventManageList> eventLists = eventManageListService.findCompletedEventManageList(memberId, status, pageable);
        CommonResponse<Page<EventManageList>> commonResponse = CommonResponse.<Page<EventManageList>>builder()
                .data(eventLists)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "지원자 대기 목록 조회", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/v1/applications/pending")
    public ResponseEntity<CommonResponse<Page<EventApplicationList.EventPendingApplication>>> getPendingEventApplicationList(@ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<EventApplicationList.EventPendingApplication> eventLists = eventApplicationListService.findPendingEventApplications(memberId, pageable);
        CommonResponse<Page<EventApplicationList.EventPendingApplication>> commonResponse = CommonResponse.<Page<EventApplicationList.EventPendingApplication>>builder()
                .data(eventLists)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "지원자 선정 목록 조회", description = "status : ALL(전체), PENDING(답변대기중), CONFIRMED(참여확정), REJECTED(참여불가)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/v1/applications/selected/status/{status}")
    public ResponseEntity<CommonResponse<Page<EventApplicationList.EventSelectedApplication>>> getSelectedEventApplicationList(@PathVariable(value = "status") String status,
                                                                                                                               @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<EventApplicationList.EventSelectedApplication> eventLists = eventApplicationListService.findSelectedEventApplications(memberId, status, pageable);
        CommonResponse<Page<EventApplicationList.EventSelectedApplication>> commonResponse = CommonResponse.<Page<EventApplicationList.EventSelectedApplication>>builder()
                .data(eventLists)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "지원자 탈락 목록 조회", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/v1/applications/rejected")
    public ResponseEntity<CommonResponse<Page<EventApplicationList.EventRejectedApplication>>> getRejectedEventApplicationList(@ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<EventApplicationList.EventRejectedApplication> eventLists = eventApplicationListService.findRejectedEventApplications(memberId, pageable);
        CommonResponse<Page<EventApplicationList.EventRejectedApplication>> commonResponse = CommonResponse.<Page<EventApplicationList.EventRejectedApplication>>builder()
                .data(eventLists)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 지원한 행사 목록 조회", description = "status : ALL(전체), RECRUITING(모집중), RECRUITMENT_CLOSED(모집마감), RECRUITMENT_CANCELLED(모집취소), SELECTED(선정), REJECTED(미선정)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/v1/trucks/{truckId}/applications/applied/status/{status}")
    public ResponseEntity<CommonResponse<Page<TruckEventApplicationList.AppliedInfo>>> getTruckEventAppliedApplicationList(@PathVariable(value = "truckId") String truckId,
                                                                                                                           @PathVariable(value = "status") String status,
                                                                                                                           @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TruckEventApplicationList.AppliedInfo> eventLists = truckEventApplicationListService.getTruckEventAppliedApplicationList(status, truckId, pageable);
        CommonResponse<Page<TruckEventApplicationList.AppliedInfo>> commonResponse = CommonResponse.<Page<TruckEventApplicationList.AppliedInfo>>builder()
                .data(eventLists)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 선정된 행사 목록 조회", description = "status : ALL(전체), CONFIRMED(진행중) PENDING(답변대기중), REJECTED(참석불가), COMPLETED(종료됨)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/v1/trucks/{truckId}/applications/selected/status/{status}")
    public ResponseEntity<CommonResponse<Page<TruckEventApplicationList.SelectedInfo>>> getTruckEventSelectedApplicationList(@PathVariable(value = "truckId") String truckId,
                                                                                                                             @PathVariable(value = "status") String status,
                                                                                                                             @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TruckEventApplicationList.SelectedInfo> selectedApplicationList = truckEventApplicationListService.getTruckEventSelectedApplicationList(status, truckId, pageable);
        CommonResponse<Page<TruckEventApplicationList.SelectedInfo>> commonResponse = CommonResponse.<Page<TruckEventApplicationList.SelectedInfo>>builder()
                .data(selectedApplicationList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }


    @Operation(summary = "행사 지원할 푸드트럭 목록 조회", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/v1/{eventId}/applicable/trucks")
    public ResponseEntity<CommonResponse<Page<EventApplicableTruckList>>> getApplicableTruckList(@PathVariable(value = "eventId") String eventId,
                                                                                                 @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<EventApplicableTruckList> eventApplicableTruckLists = eventApplicableTruckListService.findApplicableTrucks(eventId, memberId, pageable);
        CommonResponse<Page<EventApplicableTruckList>> commonResponse = CommonResponse.<Page<EventApplicableTruckList>>builder()
                .data(eventApplicableTruckLists)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

}
