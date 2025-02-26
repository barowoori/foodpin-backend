package com.barowoori.foodpinbackend.region.command.domain.repository.dto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegionCode {
    private String code;
    private String name;

    public static RegionCode of(String code, String name){
        return RegionCode.builder()
                .code(code)
                .name(name)
                .build();
    }
}
