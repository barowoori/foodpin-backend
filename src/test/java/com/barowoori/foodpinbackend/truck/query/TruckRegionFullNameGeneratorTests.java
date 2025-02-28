package com.barowoori.foodpinbackend.truck.query;

import com.barowoori.foodpinbackend.region.command.domain.model.*;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGuRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGunRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionSiRepository;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckRegion;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRegionRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import com.barowoori.foodpinbackend.truck.query.application.TruckRegionFullNameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class TruckRegionFullNameGeneratorTests {
    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private RegionDoRepository regionDoRepository;
    @Autowired
    private RegionSiRepository regionSiRepository;
    @Autowired
    private RegionGuRepository regionGuRepository;
    @Autowired
    private RegionGunRepository regionGunRepository;
    @Autowired
    private TruckRegionRepository truckRegionRepository;
    @Autowired
    private TruckRegionFullNameGenerator truckRegionFullNameGenerator;

    Truck truck;
    RegionDo gyeonggi;
    RegionSi yongIn;

    @BeforeEach
    void setUp() {

        truck = Truck.builder()
                .name("바로우리")
                .description("바로우리 트럭입니다")
                .isDeleted(Boolean.FALSE)
                .views(100)
                .build();
        truck = truckRepository.save(truck);

        gyeonggi = new RegionDo.Builder().addName("경기도").build();
        gyeonggi = regionDoRepository.save(gyeonggi);
        RegionDo gangwon = new RegionDo.Builder().addName("강원도").build();
        gangwon = regionDoRepository.save(gangwon);

        yongIn = new RegionSi.Builder().addRegionDo(gyeonggi).addName("용인시").build();
        yongIn = regionSiRepository.save(yongIn);

        RegionSi anyang = new RegionSi.Builder().addRegionDo(gyeonggi).addName("안양시").build();
        anyang = regionSiRepository.save(anyang);

        RegionSi wonju = new RegionSi.Builder().addRegionDo(gangwon).addName("원주시").build();
        wonju = regionSiRepository.save(wonju);

        RegionSi seoul = new RegionSi.Builder().addName("서울특별시").build();
        seoul = regionSiRepository.save(seoul);

        RegionGu giheung = new RegionGu.Builder().addRegionDo(gyeonggi).addRegionSi(yongIn).addName("기흥구").build();
        giheung = regionGuRepository.save(giheung);

        RegionGu suji = new RegionGu.Builder().addRegionDo(gyeonggi).addRegionSi(yongIn).addName("수지구").build();
        suji = regionGuRepository.save(suji);

        RegionGu dongan = new RegionGu.Builder().addRegionDo(gyeonggi).addRegionSi(anyang).addName("동안구").build();
        dongan = regionGuRepository.save(dongan);

        RegionGu mapo = new RegionGu.Builder().addRegionSi(seoul).addName("마포구").build();
        mapo = regionGuRepository.save(mapo);

        RegionGun gapyeong = new RegionGun.Builder().addRegionDo(gyeonggi).addName("가평군").build();
        gapyeong = regionGunRepository.save(gapyeong);

        RegionSi incheon = new RegionSi.Builder().addName("인천광역시").build();
        incheon = regionSiRepository.save(incheon);

        RegionGun ganghwa = new RegionGun.Builder().addRegionSi(incheon).addName("강화군").build();
        ganghwa = regionGunRepository.save(ganghwa);

        TruckRegion truckRegion = TruckRegion.builder()
                .regionId(giheung.getId())
                .regionType(RegionType.REGION_GU)
                .truck(truck)
                .build();
        truckRegion = truckRegionRepository.save(truckRegion);

        TruckRegion truckRegion1 = TruckRegion.builder()
                .regionId(gapyeong.getId())
                .regionType(RegionType.REGION_GUN)
                .truck(truck)
                .build();
        truckRegion1 = truckRegionRepository.save(truckRegion1);


        TruckRegion truckRegion2 = TruckRegion.builder()
                .regionId(ganghwa.getId())
                .regionType(RegionType.REGION_GUN)
                .truck(truck)
                .build();
        truckRegion2 = truckRegionRepository.save(truckRegion2);

        TruckRegion truckRegion3 = TruckRegion.builder()
                .regionId(mapo.getId())
                .regionType(RegionType.REGION_GU)
                .truck(truck)
                .build();
        truckRegion3 = truckRegionRepository.save(truckRegion3);

        TruckRegion truckRegion4 = TruckRegion.builder()
                .regionId(seoul.getId())
                .regionType(RegionType.REGION_SI)
                .truck(truck)
                .build();
        truckRegion4 = truckRegionRepository.save(truckRegion4);
    }

    @Test
    void When_GetTruckRegionName(){
       List<String> result =  truckRegionFullNameGenerator.findRegionNamesByTruckId(truck.getId());
       for(String region : result){
           System.out.println("region : "+ region);
       }
    }


}
