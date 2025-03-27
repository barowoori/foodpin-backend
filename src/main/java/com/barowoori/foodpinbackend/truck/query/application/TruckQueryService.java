package com.barowoori.foodpinbackend.truck.query.application;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.truck.command.application.dto.ResponseTruck;
import com.barowoori.foodpinbackend.truck.command.domain.exception.TruckErrorCode;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManager;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckManagerRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TruckQueryService {
    private final MemberRepository memberRepository;
    private final TruckManagerRepository truckManagerRepository;

    @Transactional(readOnly = true)
    public List<ResponseTruck.GetTruckNameDto> getOwnedTruck(){
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        List<Truck> trucks = truckManagerRepository.findOwnedTrucks(member.getId());
        return trucks.stream().map(ResponseTruck.GetTruckNameDto::of).toList();
    }
}
