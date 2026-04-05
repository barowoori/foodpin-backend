package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TruckRepositoryCustom {
    Truck getTruckWithPhotoById(String id);
    Integer findMaxAvgMenuPrice();
    Page<Truck> findTruckListByFilter(String searchTerm, List<String> categoryCodes, Map<RegionType, List<String>> regionIds,
                                      Set<TruckType> types, Integer minAvgMenuPrice,  Integer maxAvgMenuPrice, Set<TruckColor> colors, Set<TruckBodyType> bodyTypes,
                                      Set<PaymentMethod> paymentMethods, Set<ProofIssuanceType> proofIssuanceTypes, Boolean isCatering,
                                      Pageable pageable);
    Page<Truck> findLikeTruckListByFilter(String memberId, String searchTerm, List<String> categoryCodes, Map<RegionType, List<String>> regionIds,
                                          Set<TruckType> types, Integer minAvgMenuPrice,  Integer maxAvgMenuPrice, Set<TruckColor> colors, Set<TruckBodyType> bodyTypes,
                                          Set<PaymentMethod> paymentMethods, Set<ProofIssuanceType> proofIssuanceTypes, Boolean isCatering,
                                          Pageable pageable);
    Page<Truck> findApplicableTrucks(String memberId, Pageable pageable);
    List<Truck> findAllApplicableTrucks(String memberId);
}
