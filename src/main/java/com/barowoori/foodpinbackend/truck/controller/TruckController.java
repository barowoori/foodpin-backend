package com.barowoori.foodpinbackend.truck.controller;

import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.truck.command.application.dto.RequestTruck;
import com.barowoori.foodpinbackend.truck.command.application.service.TruckService;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDetail;
import com.barowoori.foodpinbackend.truck.query.application.TruckDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/trucks")
public class TruckController {

    private final TruckService truckService;
    private final TruckDetailService truckDetailService;

    @PostMapping(value = "/v1/new-truck", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<String>> createTruck(@Valid @RequestPart(value = "createTruckDto") RequestTruck.CreateTruckDto createTruckDto) {
        truckService.createTruck(createTruckDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @GetMapping(value = "/v1/truck-detail/{truckId}")
    public ResponseEntity<CommonResponse<TruckDetail>> getTruckDetail(@Valid @PathVariable("truckId") String truckId) {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        TruckDetail truckDetail = truckDetailService.getTruckDetail(memberId, truckId);
        CommonResponse<TruckDetail> commonResponse = CommonResponse.<TruckDetail>builder()
                .data(truckDetail)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
