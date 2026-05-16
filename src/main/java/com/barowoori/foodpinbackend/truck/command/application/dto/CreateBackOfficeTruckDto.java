package com.barowoori.foodpinbackend.truck.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Set;

@Getter
public class CreateBackOfficeTruckDto {
    @Schema(description = "소유자로 지정할 회원 ID")
    @NotBlank
    private String ownerMemberId;

    @Schema(description = "추가 운영자로 지정할 회원 ID 목록")
    private Set<String> managerMemberIds;

    @Schema(description = "생성할 트럭 정보")
    @Valid
    @NotNull
    private RequestTruck.CreateTruckDto createTruckDto;
}
