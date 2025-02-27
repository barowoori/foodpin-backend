package com.barowoori.foodpinbackend.truck.query.application;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckDocumentRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckMenuRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRegionRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckList;
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

    public TruckListService(TruckRepository truckRepository, RegionDoRepository regionDoRepository, TruckMenuRepository truckMenuRepository,
                            TruckDocumentRepository truckDocumentRepository, TruckRegionRepository truckRegionRepository,TruckRegionFullNameGenerator truckRegionFullNameGenerator,
                            ImageManager imageManager) {
        this.truckRepository = truckRepository;
        this.regionDoRepository = regionDoRepository;
        this.truckMenuRepository = truckMenuRepository;
        this.truckDocumentRepository = truckDocumentRepository;
        this.truckRegionRepository = truckRegionRepository;
        this.imageManager = imageManager;
        this.truckRegionFullNameGenerator = truckRegionFullNameGenerator;
    }

    @Transactional(readOnly = true)
    public Page<TruckList> findTruckList(List<String> regionCodes, List<String> categoryNames, String searchTerm, Pageable pageable) {
        Map<RegionType, List<String>> regionIds = regionDoRepository.findRegionIdsByFilter(regionCodes);
        Page<Truck> trucks = truckRepository.findTruckListByFilter(searchTerm, categoryNames, regionIds, pageable);
        List<String> truckIds = trucks.map(Truck::getId).stream().toList();
        Map<String, List<String>> menuNames = truckMenuRepository.getMenuNamesByTruckIds(truckIds);
        Map<String, List<DocumentType>> documents = truckDocumentRepository.getDocumentTypeByTruckIds(truckIds);
        Map<String, List<String>> regionNames = truckRegionFullNameGenerator.findRegionNamesByTruckIds(truckIds);
        return trucks.map(truck -> TruckList.of(truck, documents.get(truck.getId()), regionNames.get(truck.getId()), menuNames.get(truck.getId()), imageManager));
    }

    @Transactional(readOnly = true)
    public Page<TruckList> findLikeTruckByTruckList(String memberId, List<String> regionCodes, List<String> categoryNames, String searchTerm, Pageable pageable) {
        Map<RegionType, List<String>> regionIds = regionDoRepository.findRegionIdsByFilter(regionCodes);
        Page<Truck> trucks = truckRepository.findLikeTruckListByFilter(memberId, searchTerm, categoryNames, regionIds, pageable);
        List<String> truckIds = trucks.map(Truck::getId).stream().toList();
        Map<String, List<String>> menuNames = truckMenuRepository.getMenuNamesByTruckIds(truckIds);
        Map<String, List<DocumentType>> documents = truckDocumentRepository.getDocumentTypeByTruckIds(truckIds);
        Map<String, List<String>> regionNames = truckRegionFullNameGenerator.findRegionNamesByTruckIds(truckIds);
        return trucks.map(truck -> TruckList.of(truck, documents.get(truck.getId()), regionNames.get(truck.getId()), menuNames.get(truck.getId()), imageManager));
    }
}
