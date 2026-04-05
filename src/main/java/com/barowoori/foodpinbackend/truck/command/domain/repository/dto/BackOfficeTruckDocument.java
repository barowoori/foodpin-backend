package com.barowoori.foodpinbackend.truck.command.domain.repository.dto;

import com.barowoori.foodpinbackend.document.command.domain.model.BusinessRegistration;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocument;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BackOfficeTruckDocument {
    private TruckDocument truckDocument;
    private Member member;
    private BusinessRegistration businessRegistration;
}
