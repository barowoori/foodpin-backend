package com.barowoori.foodpinbackend.region.domain.repository;

import com.barowoori.foodpinbackend.region.command.domain.model.*;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGuRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGunRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionSiRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class RegionDoRepositoryTests {
    @Autowired
    private RegionDoRepository regionDoRepository;
    @Autowired
    private RegionSiRepository regionSiRepository;
    @Autowired
    private RegionGuRepository regionGuRepository;
    @Autowired
    private RegionGunRepository regionGunRepository;

    RegionDo gyeonggi;
    RegionSi yongIn;

    @BeforeEach
    void setUp() {
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

        RegionGu giheung = new RegionGu.Builder().addRegionDo(gyeonggi).addRegionSi(yongIn).addName("기흥구").build();
        giheung = regionGuRepository.save(giheung);

        RegionGu suji = new RegionGu.Builder().addRegionDo(gyeonggi).addRegionSi(yongIn).addName("수지구").build();
        suji = regionGuRepository.save(suji);

        RegionGu dongan = new RegionGu.Builder().addRegionDo(gyeonggi).addRegionSi(anyang).addName("동안구").build();
        dongan = regionGuRepository.save(dongan);

        RegionGun gapyeong = new RegionGun.Builder().addRegionDo(gyeonggi).addName("가평군").build();
        gapyeong = regionGunRepository.save(gapyeong);

    }

    @Test
    @DisplayName("지역코드가 도가 하위일 때는 도를 반환한다")
    void When_Region_Type_DO_Then_ReturnDo(){
        RegionInfo regionInfo = regionDoRepository.findByCode("DO" + gyeonggi.getId());
        assertEquals(RegionType.REGION_DO,regionInfo.getRegionType());
        assertEquals(gyeonggi.getId(),regionInfo.getRegionId());
    }

    @Test
    @DisplayName("지역코드가 시가 하위일 때는 시를 반환한다")
    void When_Region_Type_SI_Then_ReturnSI(){
        RegionInfo regionInfo = regionDoRepository.findByCode("SI" + yongIn.getId());
        assertEquals(RegionType.REGION_SI,regionInfo.getRegionType());
        assertEquals(yongIn.getId(),regionInfo.getRegionId());
    }

    @Test
    @DisplayName("경기도에 관련된 테이블 아이디들이 반환된다")
    void When_All_gyenggi() {
        List<String> filters = List.of("DO" + gyeonggi.getId());
        Map<RegionType, List<String>> result =  regionDoRepository.findRegionIdsByFilter(filters);
        assertEquals(1, result.get(RegionType.REGION_DO).size());
        assertEquals(2, result.get(RegionType.REGION_SI).size());
        assertEquals(3, result.get(RegionType.REGION_GU).size());
        assertEquals(1, result.get(RegionType.REGION_GUN).size());
        assertEquals(gyeonggi.getId(), result.get(RegionType.REGION_DO).stream().findFirst().get());
    }


}
