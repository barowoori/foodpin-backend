package com.barowoori.foodpinbackend.region.command.domain.query.application;

import com.barowoori.foodpinbackend.region.command.domain.model.RegionDo;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionGu;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionGun;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionSi;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGuRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGunRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionSiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegionCacheService {

    private final RegionDoRepository regionDoRepository;
    private final RegionSiRepository regionSiRepository;
    private final RegionGuRepository regionGuRepository;
    private final RegionGunRepository regionGunRepository;

    private volatile RegionSearchProcessor cachedProcessor;

    public RegionSearchProcessor buildRegionSearchProcessor() {
        if (cachedProcessor == null) {
            synchronized (this) {
                if (cachedProcessor == null) {
                    List<RegionDo> regionDos = regionDoRepository.findAll();
                    List<RegionSi> regionSis = regionSiRepository.findAll();
                    List<RegionGu> regionGus = regionGuRepository.findAll();
                    List<RegionGun> regionGuns = regionGunRepository.findAll();
                    cachedProcessor = new RegionSearchProcessor(regionDos, regionSis, regionGus, regionGuns);
                }
            }
        }
        return cachedProcessor;
    }
}
