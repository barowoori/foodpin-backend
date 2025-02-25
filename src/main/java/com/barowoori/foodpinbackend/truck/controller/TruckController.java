package com.barowoori.foodpinbackend.truck.controller;

import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.truck.command.application.dto.RequestTruck;
import com.barowoori.foodpinbackend.truck.command.application.service.TruckService;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDetail;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckList;
import com.barowoori.foodpinbackend.truck.query.application.TruckDetailService;
import com.barowoori.foodpinbackend.truck.query.application.TruckListService;
import com.barowoori.foodpinbackend.truck.query.application.TruckQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "트럭 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/trucks")
public class TruckController {

    private final TruckService truckService;
    private final TruckDetailService truckDetailService;
    private final TruckQueryService truckQueryService;
    private final TruckListService truckListService;

    @Operation(summary = "트럭 생성")
    @PostMapping(value = "/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<String>> createTruck(@Valid @RequestPart(value = "createTruckDto") RequestTruck.CreateTruckDto createTruckDto) {
        truckService.createTruck(createTruckDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 운영자 추가", description = "초대된 사람(새 운영자) 계정에서 실행, 초대된 트럭 id(초대코드) 입력")
    @PostMapping(value = "/v1/{truckId}/manager")
    public ResponseEntity<CommonResponse<String>> addManager(@Valid @PathVariable("truckId") String truckId) {
        truckService.addManager(truckId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck manager added successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 상세 정보 조회")
    @GetMapping(value = "/v1/{truckId}/detail")
    public ResponseEntity<CommonResponse<TruckDetail>> getTruckDetail(@Valid @PathVariable("truckId") String truckId) {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        TruckDetail truckDetail = truckDetailService.getTruckDetail(memberId, truckId);
        CommonResponse<TruckDetail> commonResponse = CommonResponse.<TruckDetail>builder()
                .data(truckDetail)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 목록 조회", description = "정렬 : 최신순(createdAt, DESC), 조회순(views, DESC)")
    @GetMapping(value = "/v1")
    public ResponseEntity<CommonResponse<Page<TruckList>>> getTruckList(@RequestParam(value = "region", required = false) List<String> regionCodes,
                                                                        @RequestParam(value = "category", required = false) List<String> categoryNames,
                                                                        @RequestParam(value = "search", required = false) String searchTerm,
                                                                        @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TruckList> truckLists = truckListService.findTruckList(regionCodes, categoryNames, searchTerm, pageable);
        CommonResponse<Page<TruckList>> commonResponse = CommonResponse.<Page<TruckList>>builder()
                .data(truckLists)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "찜한 트럭 목록 조회", description = "정렬 : 최신순(createdAt, DESC), 조회순(views, DESC)")
    @GetMapping(value = "/v1/like")
    public ResponseEntity<CommonResponse<Page<TruckList>>> getLikeTruckList(@RequestParam(value = "region", required = false) List<String> regionCodes,
                                                                            @RequestParam(value = "category", required = false) List<String> categoryNames,
                                                                            @RequestParam(value = "search", required = false) String searchTerm,
                                                                            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<TruckList> truckLists = truckListService.findLikeTruckByTruckList(memberId, regionCodes, categoryNames, searchTerm, pageable);
        CommonResponse<Page<TruckList>> commonResponse = CommonResponse.<Page<TruckList>>builder()
                .data(truckLists)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }


    @Operation(summary = "본인 소유 트럭 리스트 조회", description = "소유자 뿐만 아니라 운영자도 트럭 조회 가능")
    @GetMapping(value = "/v1/owned")
    public ResponseEntity<CommonResponse<List<TruckDetail.TruckInfo>>> getOwnedTruck() {
        List<TruckDetail.TruckInfo> truckInfoList = truckQueryService.getOwnedTruck();
        CommonResponse<List<TruckDetail.TruckInfo>> commonResponse = CommonResponse.<List<TruckDetail.TruckInfo>>builder()
                .data(truckInfoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 기본 정보 수정")
    @PutMapping(value = "/v1/{truckId}/info")
    public ResponseEntity<CommonResponse<String>> updateTruckInfo(@Valid @PathVariable("truckId") String truckId,
                                                                  @RequestBody RequestTruck.UpdateTruckInfoDto updateTruckInfoDto) {
        truckService.updateTruckInfo(truckId, updateTruckInfoDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck info updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 운영 정보 수정")
    @PutMapping(value = "/v1/{truckId}/operation")
    public ResponseEntity<CommonResponse<String>> updateTruckOperation(@Valid @PathVariable("truckId") String truckId,
                                                                       @RequestBody RequestTruck.UpdateTruckOperationDto updateTruckOperationDto) {
        truckService.updateTruckOperation(truckId, updateTruckOperationDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck operation updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 카테고리 및 메뉴 수정")
    @PutMapping(value = "/v1/{truckId}/menu")
    public ResponseEntity<CommonResponse<String>> updateTruckMenu(@Valid @PathVariable("truckId") String truckId,
                                                                  @RequestBody RequestTruck.UpdateTruckMenuDto updateTruckMenuDto) {
        truckService.updateTruckMenu(truckId, updateTruckMenuDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck menu updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 소유자 변경", description = "트럭 소유자 계정에서만 동작, 권한 넘겨줄 트럭 id와 운영자 id 입력")
    @PutMapping(value = "/v1/{truckId}/owner")
    public ResponseEntity<CommonResponse<String>> changeOwner(@Valid @PathVariable("truckId") String truckId,
                                                              @Valid @RequestParam String managerId) {
        truckService.changeOwner(managerId, truckId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck owner changed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 운영자 삭제", description = "트럭 소유자 계정에서만 동작, 삭제할 운영자 id 입력")
    @DeleteMapping(value = "/v1/{truckId}/manager")
    public ResponseEntity<CommonResponse<String>> deleteManager(@Valid @PathVariable("truckId") String truckId,
                                                                @Valid @RequestParam String managerId) {
        truckService.deleteManager(managerId, truckId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck manager deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 삭제", description = "isDeleted 상태값만 변경, 조회 시 isDeleted = true는 안 나오게 로직 추가 수정 필요")
    @DeleteMapping(value = "/v1/{truckId}")
    public ResponseEntity<CommonResponse<String>> deleteTruck(@Valid @PathVariable("truckId") String truckId) {
        truckService.deleteTruck(truckId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
