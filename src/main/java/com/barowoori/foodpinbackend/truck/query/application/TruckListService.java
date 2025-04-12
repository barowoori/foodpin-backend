package com.barowoori.foodpinbackend.truck.query.application;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.region.command.domain.model.*;
import com.barowoori.foodpinbackend.region.command.domain.query.application.RegionSearchProcessor;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGuRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGunRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionSiRepository;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckDocumentRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckMenuRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRegionRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckList;
import org.junit.Before;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
public class TruckListService {
    private final TruckRepository truckRepository;
    private final RegionDoRepository regionDoRepository;
    private final TruckMenuRepository truckMenuRepository;
    private final TruckDocumentRepository truckDocumentRepository;
    private final TruckRegionRepository truckRegionRepository;
    private final ImageManager imageManager;
    private final TruckRegionFullNameGenerator truckRegionFullNameGenerator;
    private final RegionSiRepository regionSiRepository;
    private final RegionGuRepository regionGuRepository;
    private final RegionGunRepository regionGunRepository;

    public TruckListService(TruckRepository truckRepository, RegionDoRepository regionDoRepository, TruckMenuRepository truckMenuRepository,
                            TruckDocumentRepository truckDocumentRepository, TruckRegionRepository truckRegionRepository,TruckRegionFullNameGenerator truckRegionFullNameGenerator,
                            ImageManager imageManager,RegionSiRepository regionSiRepository,RegionGuRepository regionGuRepository,RegionGunRepository regionGunRepository) {
        this.truckRepository = truckRepository;
        this.regionDoRepository = regionDoRepository;
        this.truckMenuRepository = truckMenuRepository;
        this.truckDocumentRepository = truckDocumentRepository;
        this.truckRegionRepository = truckRegionRepository;
        this.imageManager = imageManager;
        this.truckRegionFullNameGenerator = truckRegionFullNameGenerator;
        this.regionSiRepository = regionSiRepository;
        this.regionGuRepository = regionGuRepository;
        this.regionGunRepository = regionGunRepository;

    }
    private RegionSearchProcessor getRegionSearchProcessor(){
        List<RegionDo> regionDos = regionDoRepository.findAll();
        List<RegionSi> regionSis = regionSiRepository.findAll();
        List<RegionGu> regionGus = regionGuRepository.findAll();
        List<RegionGun> regionGuns = regionGunRepository.findAll();

        return new RegionSearchProcessor(regionDos, regionSis, regionGus, regionGuns);
    }

    @Transactional(readOnly = true)
    public Page<TruckList> findTruckList(List<String> regionCodes, List<String> categoryNames, String searchTerm, Pageable pageable) {
        RegionSearchProcessor regionSearchProcessor = getRegionSearchProcessor();

        Map<RegionType, List<String>> regionIds = regionDoRepository.findRegionIdsByFilter(regionCodes);
        Page<Truck> trucks = truckRepository.findTruckListByFilter(searchTerm, categoryNames, regionIds, pageable);
        List<String> truckIds = trucks.map(Truck::getId).stream().toList();
        Map<String, List<DocumentType>> documents = truckDocumentRepository.getDocumentTypeByTruckIds(truckIds);
        return trucks.map(truck -> TruckList.of(truck, documents.get(truck.getId()), truck.getTruckRegionNames(regionSearchProcessor), imageManager));
    }

    @Transactional(readOnly = true)
    public Page<TruckList> findLikeTruckByTruckList(String memberId, List<String> regionCodes, List<String> categoryNames, String searchTerm, Pageable pageable) {
        RegionSearchProcessor regionSearchProcessor = getRegionSearchProcessor();

        Map<RegionType, List<String>> regionIds = regionDoRepository.findRegionIdsByFilter(regionCodes);
        Page<Truck> trucks = truckRepository.findLikeTruckListByFilter(memberId, searchTerm, categoryNames, regionIds, pageable);
        List<String> truckIds = trucks.map(Truck::getId).stream().toList();
        Map<String, List<DocumentType>> documents = truckDocumentRepository.getDocumentTypeByTruckIds(truckIds);
        return trucks.map(truck -> TruckList.of(truck, documents.get(truck.getId()), truck.getTruckRegionNames(regionSearchProcessor),imageManager));
    }
}
