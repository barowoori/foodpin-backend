package com.barowoori.foodpinbackend.document.controller;

import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.document.command.application.dto.ResponseDocument;
import com.barowoori.foodpinbackend.document.command.application.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "서류 API", description = "서류 API")
@RequiredArgsConstructor
@RequestMapping("/api/documents")
@RestController
public class DocumentController {
    private final DocumentService documentService;

    @Operation(summary = "사업자 진위 여부 확인", description = "사업자 진위 여부 확인")
    @GetMapping(value = "/v1/business-number/valid")
    public ResponseEntity<CommonResponse<ResponseDocument.validDto>> validBusinessNumber(@Valid @RequestParam(name = "business-number") String businessNumber,
                                                                                   @Valid @RequestParam(name = "representative-name") String representativeName,
                                                                                   @Valid @RequestParam(name = "opening-date") LocalDate openingDate){
        ResponseDocument.validDto response = documentService.validateBusinessNumber(businessNumber,representativeName, openingDate);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<ResponseDocument.validDto>builder()
                        .data(response).build());
    }
}
