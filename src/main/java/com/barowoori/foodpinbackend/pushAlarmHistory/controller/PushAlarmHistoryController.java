package com.barowoori.foodpinbackend.pushAlarmHistory.controller;

import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.common.exception.ErrorResponse;
import com.barowoori.foodpinbackend.member.command.application.dto.RequestMember;
import com.barowoori.foodpinbackend.pushAlarmHistory.command.application.dto.ResponsePushAlarmHistory;
import com.barowoori.foodpinbackend.pushAlarmHistory.command.application.service.PushAlarmHistoryService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "푸시알림 API", description = "푸시 알림 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/push-alarms")
@RestController
public class PushAlarmHistoryController {

    private final PushAlarmHistoryService pushAlarmHistoryService;

    @Operation(summary = "푸시알림 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/v1/histories")
    public ResponseEntity<CommonResponse<Page<ResponsePushAlarmHistory.GetPushAlarmHistory>>> getPushAlarmHistories(@ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<ResponsePushAlarmHistory.GetPushAlarmHistory> response = pushAlarmHistoryService.getPushAlarmHistories(memberId, pageable);
        CommonResponse<Page<ResponsePushAlarmHistory.GetPushAlarmHistory>> commonResponse = CommonResponse.<Page<ResponsePushAlarmHistory.GetPushAlarmHistory>>builder()
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
