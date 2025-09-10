package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface TruckRepositoryCustom {
    Truck getTruckWithPhotoById(String id);
    Page<Truck> findTruckListByFilter(String searchTerm, List<String> categoryNames, Map<RegionType, List<String>> regionIds, Pageable pageable);
    Page<Truck> findLikeTruckListByFilter(String memberId, String searchTerm, List<String> categoryCodes, Map<RegionType, List<String>> regionIds, Pageable pageable);
    Page<Truck> findApplicableTrucks(String memberId, Pageable pageable);
    List<Truck> findAllApplicableTrucks(String memberId);
}
