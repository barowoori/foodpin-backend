package com.barowoori.foodpinbackend.truck.command.domain.repository.dto;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManagerRole;
import lombok.Getter;

@Getter
public class TruckManagerSummary {
    private String truckManagerId;
    private String nickname;
    private String phone;
    private String image;
    private TruckManagerRole role;
}
