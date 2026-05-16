package com.barowoori.foodpinbackend.member.controller;

import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.common.exception.ErrorResponse;
import com.barowoori.foodpinbackend.member.command.application.dto.ResponseBackOfficeMember;
import com.barowoori.foodpinbackend.member.command.application.dto.SearchBackOfficeMemberDto;
import com.barowoori.foodpinbackend.member.query.application.BackOfficeMemberListService;
import com.barowoori.foodpinbackend.statistics.query.application.BackOfficeSignupStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "회원 백오피스 API", description = "회원 백오피스 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberBackOfficeController {

    private final BackOfficeMemberListService backOfficeMemberListService;
    private final BackOfficeSignupStatisticsService backOfficeSignupStatisticsService;

    @Operation(summary = "백오피스 회원 목록 조회", description = "정렬 : 최신순(createdAt, DESC)"
            + "\n\nsearch는 닉네임, 전화번호, 이메일 기준으로 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/v1/backoffice")
    public ResponseEntity<CommonResponse<Page<ResponseBackOfficeMember.GetBackOfficeMemberListDto>>> getBackOfficeMemberList(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "isDeleted", required = false) Boolean isDeleted,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ResponseBackOfficeMember.GetBackOfficeMemberListDto> members =
                backOfficeMemberListService.findBackOfficeMemberList(search, isDeleted, pageable);

        CommonResponse<Page<ResponseBackOfficeMember.GetBackOfficeMemberListDto>> commonResponse =
                CommonResponse.<Page<ResponseBackOfficeMember.GetBackOfficeMemberListDto>>builder()
                        .data(members)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "회원 검색", description = "닉네임, 전화번호 기준으로 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/v1/backoffice/search")
    public ResponseEntity<CommonResponse<Page<SearchBackOfficeMemberDto>>> searchBackOfficeMembers(
            @RequestParam(value = "search", required = false) String search,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<SearchBackOfficeMemberDto> members =
                backOfficeMemberListService.searchBackOfficeMembersForTruckManager(search, pageable);

        CommonResponse<Page<SearchBackOfficeMemberDto>> commonResponse =
                CommonResponse.<Page<SearchBackOfficeMemberDto>>builder()
                        .data(members)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "백오피스 가입자수 대시보드 조회", description = "unit은 DAY, WEEK, MONTH 지원")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/v1/backoffice/dashboard/signups")
    public ResponseEntity<CommonResponse<ResponseBackOfficeMember.BackOfficeSignupStatisticsDto>> getBackOfficeSignupStatistics(
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to,
            @RequestParam("unit") ResponseBackOfficeMember.SignupStatisticsUnit unit) {

        ResponseBackOfficeMember.BackOfficeSignupStatisticsDto statistics =
                backOfficeSignupStatisticsService.getSignupStatistics(from, to, unit);

        CommonResponse<ResponseBackOfficeMember.BackOfficeSignupStatisticsDto> commonResponse =
                CommonResponse.<ResponseBackOfficeMember.BackOfficeSignupStatisticsDto>builder()
                        .data(statistics)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "오늘 신규 가입 회원 수 조회", description = "오늘 날짜 기준으로 members + Redis를 실시간 집계")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/v1/backoffice/dashboard/signups/today")
    public ResponseEntity<CommonResponse<ResponseBackOfficeMember.TodaySignupStatisticsDto>> getTodaySignupStatistics() {
        ResponseBackOfficeMember.TodaySignupStatisticsDto statistics =
                backOfficeSignupStatisticsService.getTodaySignupStatistics();

        CommonResponse<ResponseBackOfficeMember.TodaySignupStatisticsDto> commonResponse =
                CommonResponse.<ResponseBackOfficeMember.TodaySignupStatisticsDto>builder()
                        .data(statistics)
                        .build();

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
