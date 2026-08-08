package com.barowoori.foodpinbackend.truck.query.application;

import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.region.command.domain.model.*;
import com.barowoori.foodpinbackend.region.command.domain.query.application.RegionCacheService;
import com.barowoori.foodpinbackend.region.command.domain.query.application.RegionSearchProcessor;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckDocumentRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDocumentInfoDto;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckList;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
public class TruckListService {
    private final TruckRepository truckRepository;
    private final RegionDoRepository regionDoRepository;
    private final TruckDocumentRepository truckDocumentRepository;
    private final ImageManager imageManager;
    private final TruckRegionFullNameGenerator truckRegionFullNameGenerator;
    private final RegionCacheService regionCacheService;

    public TruckListService(TruckRepository truckRepository, RegionDoRepository regionDoRepository,
                            TruckDocumentRepository truckDocumentRepository,
                            TruckRegionFullNameGenerator truckRegionFullNameGenerator,
                            ImageManager imageManager, RegionCacheService regionCacheService) {
        this.truckRepository = truckRepository;
        this.regionDoRepository = regionDoRepository;
        this.truckDocumentRepository = truckDocumentRepository;
        this.imageManager = imageManager;
        this.truckRegionFullNameGenerator = truckRegionFullNameGenerator;
        this.regionCacheService = regionCacheService;
    }

    private RegionSearchProcessor getRegionSearchProcessor() {
        return regionCacheService.buildRegionSearchProcessor();
    }

    @Transactional(readOnly = true)
    public Page<TruckList> findTruckList(List<String> regionCodes,
                                         List<String> categoryNames,
                                         String searchTerm,
                                         Set<TruckType> types,
                                         Integer minAvgMenuPrice,
                                         Integer maxAvgMenuPrice,
                                         Set<TruckColor> colors,
                                         Set<TruckBodyType> bodyTypes,
                                         Set<PaymentMethod> paymentMethods,
                                         Set<ProofIssuanceType> proofIssuanceTypes,
                                         Boolean isCatering,
                                         Pageable pageable) {
        RegionSearchProcessor regionSearchProcessor = getRegionSearchProcessor();
        Map<RegionType, List<String>> regionIds = regionDoRepository.findRegionIdsByFilter(regionCodes);
        Page<TruckProjection> projections = truckRepository.findTruckListByFilter(searchTerm, categoryNames, regionIds, types, minAvgMenuPrice, maxAvgMenuPrice,
                colors, bodyTypes, paymentMethods, proofIssuanceTypes, isCatering, pageable);
        List<String> truckIds = projections.getContent().stream().map(TruckProjection::getId).toList();
        Map<String, List<TruckDocumentInfoDto>> documents = truckDocumentRepository.getDocumentTypeByTruckIds(truckIds);

        return projections.map(projection -> {
            List<String> regionNames = projection.getRegions().stream()
                    .map(r -> regionSearchProcessor.findFullRegionName(r.regionType(), r.regionId()))
                    .filter(Objects::nonNull)
                    .toList();
            String regionList = truckRegionFullNameGenerator.makeRegionListByRegionNames(regionNames);
            return TruckList.of(projection, documents.get(projection.getId()), regionNames, regionList, imageManager);
        });
    }

    @Transactional(readOnly = true)
    public Page<TruckList> findLikeTruckByTruckList(String memberId, List<String> regionCodes,
                                                     List<String> categoryNames,
                                                     String searchTerm,
                                                     Set<TruckType> types,
                                                     Integer minAvgMenuPrice,
                                                     Integer maxAvgMenuPrice,
                                                     Set<TruckColor> colors,
                                                     Set<TruckBodyType> bodyTypes,
                                                     Set<PaymentMethod> paymentMethods,
                                                     Set<ProofIssuanceType> proofIssuanceTypes,
                                                     Boolean isCatering,
                                                     Pageable pageable) {
        RegionSearchProcessor regionSearchProcessor = getRegionSearchProcessor();
        Map<RegionType, List<String>> regionIds = regionDoRepository.findRegionIdsByFilter(regionCodes);
        Page<TruckProjection> projections = truckRepository.findLikeTruckListByFilter(memberId, searchTerm, categoryNames, regionIds, types, minAvgMenuPrice, maxAvgMenuPrice,
                colors, bodyTypes, paymentMethods, proofIssuanceTypes, isCatering, pageable);
        List<String> truckIds = projections.getContent().stream().map(TruckProjection::getId).toList();
        Map<String, List<TruckDocumentInfoDto>> documents = truckDocumentRepository.getDocumentTypeByTruckIds(truckIds);

        return projections.map(projection -> {
            List<String> regionNames = projection.getRegions().stream()
                    .map(r -> regionSearchProcessor.findFullRegionName(r.regionType(), r.regionId()))
                    .filter(Objects::nonNull)
                    .toList();
            String regionList = truckRegionFullNameGenerator.makeRegionListByRegionNames(regionNames);
            return TruckList.of(projection, documents.get(projection.getId()), regionNames, regionList, imageManager);
        });
    }

    @Transactional(readOnly = true)
    public Page<TruckList> findBackOfficeTruckList(List<String> regionCodes,
                                                    List<String> categoryNames,
                                                    String searchTerm,
                                                    Set<TruckType> types,
                                                    Integer minAvgMenuPrice,
                                                    Integer maxAvgMenuPrice,
                                                    Set<TruckColor> colors,
                                                    Set<TruckBodyType> bodyTypes,
                                                    Set<PaymentMethod> paymentMethods,
                                                    Set<ProofIssuanceType> proofIssuanceTypes,
                                                    Boolean isCatering,
                                                    Boolean isDeleted,
                                                    Pageable pageable) {
        RegionSearchProcessor regionSearchProcessor = getRegionSearchProcessor();
        Map<RegionType, List<String>> regionIds = regionDoRepository.findRegionIdsByFilter(regionCodes);
        Page<TruckProjection> projections = truckRepository.findBackOfficeTruckListByFilter(searchTerm, categoryNames, regionIds, types, minAvgMenuPrice, maxAvgMenuPrice,
                colors, bodyTypes, paymentMethods, proofIssuanceTypes, isCatering, isDeleted, pageable);
        List<String> truckIds = projections.getContent().stream().map(TruckProjection::getId).toList();
        Map<String, List<TruckDocumentInfoDto>> documents = truckDocumentRepository.getDocumentTypeByTruckIds(truckIds);

        return projections.map(projection -> {
            List<String> regionNames = projection.getRegions().stream()
                    .map(r -> regionSearchProcessor.findFullRegionName(r.regionType(), r.regionId()))
                    .filter(Objects::nonNull)
                    .toList();
            String regionList = truckRegionFullNameGenerator.makeRegionListByRegionNames(regionNames);
            return TruckList.of(projection, documents.get(projection.getId()), regionNames, regionList, imageManager);
        });
    }
}
