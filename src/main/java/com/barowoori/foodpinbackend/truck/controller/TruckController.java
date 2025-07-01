package com.barowoori.foodpinbackend.truck.controller;

import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.common.exception.ErrorResponse;
import com.barowoori.foodpinbackend.truck.command.application.dto.RequestTruck;
import com.barowoori.foodpinbackend.truck.command.application.dto.ResponseTruck;
import com.barowoori.foodpinbackend.truck.command.application.service.TruckService;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDetail;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckList;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckManagerSummary;
import com.barowoori.foodpinbackend.truck.query.application.TruckDetailService;
import com.barowoori.foodpinbackend.truck.query.application.TruckListService;
import com.barowoori.foodpinbackend.truck.query.application.TruckQueryService;
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

    @Operation(summary = "트럭 생성", description = "사진의 경우 파일 저장 api로 업로드 후 반환된 파일 id 리스트로 전달")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "트럭 사진 파일을 못 찾을 경우[30001], " +
                    "트럭 카테고리가 없을 경우[30007], 트럭 메뉴 사진 파일을 못 찾을 경우[30002], " +
                    "트럭 서류 사진 파일을 못 찾을 경우[30003], 멤버를 못 찾을 경우[20004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/v1")
    public ResponseEntity<CommonResponse<String>> createTruck(@Valid @RequestBody RequestTruck.CreateTruckDto createTruckDto) {
        truckService.createTruck(createTruckDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 운영자 추가", description = "초대된 사람(새 운영자) 계정에서 실행, 초대된 트럭 id(초대코드) 입력")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "매니저가 이미 등록되어 있는 경우[30006], " +
                    "초대 코드가 일치하지 않는 경우[30010]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "멤버를 못 찾을 경우[20004], " +
                    "트럭을 못 찾을 경우[30000]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/v1/manager")
    public ResponseEntity<CommonResponse<String>> addManager(@RequestBody RequestTruck.AddManagerDto addManagerDto) {
        truckService.addManager(addManagerDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck manager added successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 운영자 초대 문구 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "매니저가 이미 등록되어 있는 경우[30006], " +
                    "초대 코드가 일치하지 않는 경우[30010]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "멤버를 못 찾을 경우[20004], " +
                    "트럭을 못 찾을 경우[30000]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/v1/{truckId}/manager/invite-message")
    public ResponseEntity<CommonResponse<ResponseTruck.GetTruckInviteMessageDto>> getManagerInviteMessage(@Valid @PathVariable("truckId") String truckId) {
        ResponseTruck.GetTruckInviteMessageDto response = truckService.getManagerInviteMessage(truckId);
        CommonResponse<ResponseTruck.GetTruckInviteMessageDto> commonResponse = CommonResponse.<ResponseTruck.GetTruckInviteMessageDto>builder()
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 상세 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "트럭을 못 찾을 경우[30000]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "트럭을 못 찾을 경우[30000], " +
                    "멤버를 못 찾을 경우[20004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/v1/owned")
    public ResponseEntity<CommonResponse<List<ResponseTruck.GetTruckNameDto>>> getOwnedTruck() {
        List<ResponseTruck.GetTruckNameDto> truckInfoList = truckQueryService.getOwnedTruck();
        CommonResponse<List<ResponseTruck.GetTruckNameDto>> commonResponse = CommonResponse.<List<ResponseTruck.GetTruckNameDto>>builder()
                .data(truckInfoList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 기본 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "트럭을 못 찾을 경우[30000], " +
                    "멤버를 못 찾을 경우[20004], 트럭 사진 파일을 못 찾는 경우[30001]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "사업자 등록 정보가 누락됐을 경우[30008]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "트럭을 못 찾을 경우[30000], " +
                    "멤버를 못 찾을 경우[20004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "트럭을 못 찾을 경우[30000], " +
                    "멤버를 못 찾을 경우[20004], 카테고리를 못 찾을 경우[30007], " +
                    "메뉴 사진 파일을 못 찾을 경우[30003]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/v1/{truckId}/menu")
    public ResponseEntity<CommonResponse<String>> updateTruckMenu(@Valid @PathVariable("truckId") String truckId,
                                                                  @RequestBody RequestTruck.UpdateTruckMenuDto updateTruckMenuDto) {
        truckService.updateTruckMenu(truckId, updateTruckMenuDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck menu updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 서류 수정/신규 등록", description = "요청으로 보낸 서류 타입이 기존에 존재하면 수정, 없으면 신규 등록" +
            "\n\n사업자 등록증의 경우 사업자 정보 createBusinessRegistrationDto를, 그 외 서류의 경우 사진 파일 id 리스트를 같이 보내야 함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "사업자 등록 정보가 누락됐을 경우[30008], " +
                    "서류 사진이 누락됐을 경우[30009]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "트럭을 못 찾을 경우[30000], " +
                    "멤버를 못 찾을 경우[20004], 카테고리를 못 찾을 경우[30007], " +
                    "메뉴 사진 파일을 못 찾을 경우[30003]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/v1/{truckId}/document")
    public ResponseEntity<CommonResponse<String>> setTruckDocument(@Valid @PathVariable("truckId") String truckId,
                                                                   @RequestBody RequestTruck.TruckDocumentDto truckDocumentDto) {
        truckService.setTruckDocument(truckId, truckDocumentDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck document set successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 서류 수정/신규 등록", description = "요청으로 보낸 서류 타입이 기존에 존재하면 수정, 없으면 신규 등록" +
            "\n\n사업자 등록증의 경우 사업자 정보 createBusinessRegistrationDto를, 그 외 서류의 경우 사진 파일 id 리스트를 같이 보내야 함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "사업자 등록 정보가 누락됐을 경우[30008], " +
                    "서류 사진이 누락됐을 경우[30009]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "트럭을 못 찾을 경우[30000], " +
                    "멤버를 못 찾을 경우[20004], 카테고리를 못 찾을 경우[30007], " +
                    "메뉴 사진 파일을 못 찾을 경우[30003]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/v1/{truckId}/documents")
    public ResponseEntity<CommonResponse<String>> setTruckDocuments(@Valid @PathVariable("truckId") String truckId,
                                                                    @RequestBody List<RequestTruck.TruckDocumentDto> truckDocumentDtoList) {
        truckService.setTruckDocuments(truckId, truckDocumentDtoList);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck document set successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 소유자 변경", description = "트럭 소유자 계정에서만 동작, 권한 넘겨줄 트럭 id와 운영자 id 입력")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "트럭을 못 찾을 경우[30000], " +
                    "멤버를 못 찾을 경우[20004], 트럭 소유자를 못 찾을(아닐) 경우[30005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "트럭을 못 찾을 경우[30000], " +
                    "멤버를 못 찾을 경우[20004], 트럭 소유자를 못 찾을(아닐) 경우[30005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(value = "/v1/{truckId}/manager")
    public ResponseEntity<CommonResponse<String>> deleteManager(@Valid @PathVariable("truckId") String truckId,
                                                                @Valid @RequestParam String managerId) {
        truckService.deleteManager(managerId, truckId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck manager deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 운영자 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "트럭을 못 찾을 경우[30000], " +
                    "멤버를 못 찾을 경우[20004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/v1/{truckId}/managers")
    public ResponseEntity<CommonResponse<Page<TruckManagerSummary>>> getTruckManagerList(@Valid @PathVariable("truckId") String truckId,
                                                                                         @ParameterObject @PageableDefault Pageable pageable) {

        Page<TruckManagerSummary> truckLists = truckService.getTruckManagerList(truckId, pageable);
        CommonResponse<Page<TruckManagerSummary>> commonResponse = CommonResponse.<Page<TruckManagerSummary>>builder()
                .data(truckLists)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "트럭 삭제", description = "트럭 소유자만 해당 api 사용 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "트럭을 못 찾을 경우[30000], " +
                    "멤버를 못 찾을 경우[20004], 트럭 소유자를 못 찾을(아닐) 경우[30005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(value = "/v1/{truckId}")
    public ResponseEntity<CommonResponse<String>> deleteTruck(@Valid @PathVariable("truckId") String truckId) {
        truckService.deleteTruck(truckId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
