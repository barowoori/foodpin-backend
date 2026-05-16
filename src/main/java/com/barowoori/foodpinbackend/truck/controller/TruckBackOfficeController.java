package com.barowoori.foodpinbackend.truck.controller;

import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.common.exception.ErrorResponse;
import com.barowoori.foodpinbackend.truck.command.application.dto.CreateBackOfficeTruckDto;
import com.barowoori.foodpinbackend.truck.command.application.dto.RequestTruck;
import com.barowoori.foodpinbackend.truck.command.application.dto.ResponseTruck;
import com.barowoori.foodpinbackend.truck.command.application.service.TruckService;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDetail;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckList;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckManagerSummary;
import com.barowoori.foodpinbackend.truck.query.application.TruckDetailService;
import com.barowoori.foodpinbackend.truck.query.application.TruckListService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "트럭 백오피스 API", description = "트럭 백오피스 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/trucks")
public class TruckBackOfficeController {

    private final TruckService truckService;
    private final TruckListService truckListService;
    private final TruckDetailService truckDetailService;

    @Operation(summary = "백오피스 푸드트럭 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/v1/backoffice")
    public ResponseEntity<CommonResponse<Page<TruckList>>> getBackOfficeTruckList(
            @RequestParam(value = "region", required = false) List<String> regionCodes,
            @RequestParam(value = "category", required = false) List<String> categoryCodes,
            @RequestParam(value = "search", required = false) String searchTerm,
            @RequestParam(value = "types", required = false) Set<TruckType> types,
            @RequestParam(value = "minAvgMenuPrice", required = false) Integer minAvgMenuPrice,
            @RequestParam(value = "maxAvgMenuPrice", required = false) Integer maxAvgMenuPrice,
            @RequestParam(value = "colors", required = false) Set<TruckColor> colors,
            @RequestParam(value = "bodyTypes", required = false) Set<TruckBodyType> bodyTypes,
            @RequestParam(value = "paymentMethods", required = false) Set<PaymentMethod> paymentMethods,
            @RequestParam(value = "proofIssuanceTypes", required = false) Set<ProofIssuanceType> proofIssuanceTypes,
            @RequestParam(value = "isCatering", required = false) Boolean isCatering,
            @RequestParam(value = "isDeleted", required = false) Boolean isDeleted,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TruckList> truckLists = truckListService.findBackOfficeTruckList(
                regionCodes, categoryCodes, searchTerm, types, minAvgMenuPrice, maxAvgMenuPrice, colors, bodyTypes,
                paymentMethods, proofIssuanceTypes, isCatering, isDeleted, pageable
        );

        CommonResponse<Page<TruckList>> commonResponse = CommonResponse.<Page<TruckList>>builder()
                .data(truckLists)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "백오피스 푸드트럭 상세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "트럭을 못 찾을 경우[30000]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/v1/backoffice/{truckId}")
    public ResponseEntity<CommonResponse<TruckDetail>> getBackOfficeTruckDetail(@Valid @PathVariable("truckId") String truckId) {
        TruckDetail truckDetail = truckDetailService.getBackOfficeTruckDetail(truckId);
        CommonResponse<TruckDetail> commonResponse = CommonResponse.<TruckDetail>builder()
                .data(truckDetail)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "백오피스 푸드트럭 등록 대행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "회원 또는 파일을 못 찾을 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/v1/backoffice")
    public ResponseEntity<CommonResponse<String>> createBackOfficeTruck(@Valid @RequestBody CreateBackOfficeTruckDto createBackOfficeTruckDto) {
        truckService.createTruckByAdmin(createBackOfficeTruckDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "백오피스 푸드트럭 정보 통합 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "트럭, 카테고리, 파일을 못 찾을 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/v1/backoffice/{truckId}")
    public ResponseEntity<CommonResponse<String>> updateBackOfficeTruck(@Valid @PathVariable("truckId") String truckId,
                                                                        @Valid @RequestBody RequestTruck.UpdateBackOfficeTruckDto updateBackOfficeTruckDto) {
        truckService.updateTruckByAdmin(truckId, updateBackOfficeTruckDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "백오피스 푸드트럭 관리자 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "트럭을 못 찾을 경우[30000]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/v1/backoffice/{truckId}/managers")
    public ResponseEntity<CommonResponse<Page<TruckManagerSummary>>> getBackOfficeTruckManagers(@Valid @PathVariable("truckId") String truckId,
                                                                                                 @ParameterObject @PageableDefault Pageable pageable) {
        Page<TruckManagerSummary> truckManagers = truckService.getTruckManagerListByAdmin(truckId, pageable);
        CommonResponse<Page<TruckManagerSummary>> commonResponse = CommonResponse.<Page<TruckManagerSummary>>builder()
                .data(truckManagers)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "백오피스 푸드트럭 관리자 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "트럭 또는 회원을 못 찾을 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/v1/backoffice/{truckId}/managers")
    public ResponseEntity<CommonResponse<String>> addBackOfficeTruckManager(@Valid @PathVariable("truckId") String truckId,
                                                                            @Valid @RequestBody RequestTruck.BackOfficeAssignTruckManagerDto requestDto) {
        truckService.addManagerByAdmin(truckId, requestDto.getMemberId());
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck manager added successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "백오피스 푸드트럭 소유자 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "트럭 관리자 또는 소유자를 못 찾을 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/v1/backoffice/{truckId}/owner")
    public ResponseEntity<CommonResponse<String>> changeBackOfficeTruckOwner(@Valid @PathVariable("truckId") String truckId,
                                                                             @Valid @RequestBody RequestTruck.GetTruckManagerIdDto requestDto) {
        truckService.changeOwnerByAdmin(requestDto.getTruckManagerId(), truckId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck owner changed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "백오피스 푸드트럭 관리자 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "트럭 관리자 또는 소유자를 못 찾을 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/v1/backoffice/{truckId}/managers/{managerId}")
    public ResponseEntity<CommonResponse<String>> deleteBackOfficeTruckManager(@Valid @PathVariable("truckId") String truckId,
                                                                               @Valid @PathVariable("managerId") String managerId) {
        truckService.deleteManagerByAdmin(managerId, truckId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck manager deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
