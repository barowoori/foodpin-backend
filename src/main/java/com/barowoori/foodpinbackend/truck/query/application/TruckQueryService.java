package com.barowoori.foodpinbackend.truck.query.application;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.event.command.domain.model.EventApplication;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventApplicationRepository;
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
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.barowoori.foodpinbackend.event.command.domain.model.QEvent.event;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventApplication.eventApplication;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventApplicationDate.eventApplicationDate;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventPhoto.eventPhoto;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventRecruitDetail.eventRecruitDetail;
import static com.barowoori.foodpinbackend.file.command.domain.model.QFile.file;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruck.truck;

@Service
@RequiredArgsConstructor
public class TruckQueryService {
    private final MemberRepository memberRepository;
    private final TruckManagerRepository truckManagerRepository;
    private final EventApplicationRepository eventApplicationRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional(readOnly = true)
    public List<ResponseTruck.GetTruckNameDto> getOwnedTruck(){
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        List<Truck> trucks = truckManagerRepository.findOwnedTrucks(member.getId());
        return trucks.stream().map(ResponseTruck.GetTruckNameDto::of).toList();
    }

    @Transactional(readOnly = true)
    public ResponseTruck.GetAppliedEventCountDto getTruckAppliedEventCount(String truckId){
        Integer count = jpaQueryFactory.selectDistinct(eventApplication)
                .from(eventApplication)
                .innerJoin(eventApplication.truck, truck)
                .where(truck.id.eq(truckId))
                .fetch().size();

        return ResponseTruck.GetAppliedEventCountDto.of(count);
    }
}
