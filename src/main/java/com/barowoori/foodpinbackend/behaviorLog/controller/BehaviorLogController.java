package com.barowoori.foodpinbackend.behaviorLog.controller;

import com.barowoori.foodpinbackend.behaviorLog.application.BehaviorLogService;
import com.barowoori.foodpinbackend.behaviorLog.application.dto.RequestBehaviorLog;
import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.common.exception.ErrorResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "행동 로그 API", description = "비회원 행동 로그 수집 API")
@RequiredArgsConstructor
@RequestMapping("/api/logs")
@RestController
public class BehaviorLogController {

    private final BehaviorLogService behaviorLogService;

    @Operation(summary = "비회원 행동 로그 수집",
            description = "API 호출 없이 로그인 모달만 뜨는 케이스에서 프론트가 직접 호출한다." +
                    "\n\nAuthorization 헤더의 비회원 JWT에서 sessionId를 서버가 추출하며, sessionId가 없으면(식별 불가) 적재하지 않고 200을 반환한다." +
                    "\n\nSQS 전송은 비동기라 응답과 무관하다. 프론트는 fire-and-forget으로 호출하고 실패는 무시한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공(적재 또는 식별 불가로 무시)"),
            @ApiResponse(responseCode = "400", description = "event가 누락되거나 알 수 없는 값인 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/v1/behavior")
    public ResponseEntity<CommonResponse<String>> collect(@Valid @RequestBody RequestBehaviorLog.CollectDto collectDto) {
        behaviorLogService.collect(collectDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Behavior log received.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
