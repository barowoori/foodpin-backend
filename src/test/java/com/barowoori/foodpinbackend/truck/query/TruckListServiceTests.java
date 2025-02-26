package com.barowoori.foodpinbackend.truck.query;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.category.command.domain.repository.CategoryRepository;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.repository.FileRepository;
import com.barowoori.foodpinbackend.region.command.domain.model.*;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGuRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGunRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionSiRepository;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckList;
import com.barowoori.foodpinbackend.truck.query.application.TruckListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class TruckListServiceTests {
    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private TruckListService truckListService;
    @Autowired
    private TruckMenuRepository truckMenuRepository;
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
    private CategoryRepository categoryRepository;
    @Autowired
    private TruckCategoryRepository truckCategoryRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private TruckPhotoRepository truckPhotoRepository;

    Truck truck;
    Truck deletedTruck;
    RegionSi yongIn;
    RegionSi anyang;

    @BeforeEach
    @Transactional
    void setUp() {
        truck = Truck.builder()
                .name("바로우리")
                .description("바로우리 트럭입니다")
                .isDeleted(Boolean.FALSE)
                .views(100)
                .build();
        truck = truckRepository.save(truck);

        TruckMenu truckMenu = TruckMenu.builder()
                .name("떡볶이")
                .description("맛있다")
                .price(1000)
                .truck(truck)
                .build();
        truckMenuRepository.save(truckMenu);

        TruckMenu truckMenu1 = TruckMenu.builder()
                .name("짜장면")
                .description("맛있다")
                .price(1000)
                .truck(truck)
                .build();
        truckMenuRepository.save(truckMenu1);

        Truck truck1 = Truck.builder()
                .name("푸드핀")
                .description("푸드핀 트럭입니다")
                .isDeleted(Boolean.FALSE)
                .views(0)
                .build();
        truck1 = truckRepository.save(truck1);

        TruckMenu truckMenu2 = TruckMenu.builder()
                .name("짜장면")
                .description("맛있다")
                .price(1000)
                .truck(truck1)
                .build();
        truckMenuRepository.save(truckMenu2);

        deletedTruck = Truck.builder()
                .name("삭제된 바로우리")
                .description("바로우리 트럭입니다")
                .isDeleted(Boolean.TRUE)
                .build();
        deletedTruck = truckRepository.save(deletedTruck);

        RegionDo gyeonggi = new RegionDo.Builder().addName("경기도").build();
        gyeonggi = regionDoRepository.save(gyeonggi);

        yongIn = new RegionSi.Builder().addRegionDo(gyeonggi).addName("용인시").build();
        yongIn = regionSiRepository.save(yongIn);

        anyang = new RegionSi.Builder().addRegionDo(gyeonggi).addName("안양시").build();
        anyang = regionSiRepository.save(anyang);

        RegionGu giheung = new RegionGu.Builder().addRegionDo(gyeonggi).addRegionSi(yongIn).addName("기흥구").build();
        giheung = regionGuRepository.save(giheung);

        RegionGu suji = new RegionGu.Builder().addRegionDo(gyeonggi).addRegionSi(yongIn).addName("수지구").build();
        suji = regionGuRepository.save(suji);

        RegionGu dongan = new RegionGu.Builder().addRegionDo(gyeonggi).addRegionSi(anyang).addName("동안구").build();
        dongan = regionGuRepository.save(dongan);

        RegionGun gapyeong = new RegionGun.Builder().addRegionDo(gyeonggi).addName("가평군").build();
        gapyeong = regionGunRepository.save(gapyeong);

    }

    @Nested
    @Transactional
    @DisplayName("검색어가 있을 경우에는 검색어에 해당하는 트럭 전체가 조회되어야 한다")
    class When_Exist_SearchTerm {
        @Test
        @Transactional
        @DisplayName("푸드트럭명에 검색어가 포함되어 있는 경우 해당 트럭 리스트 조회한다")
        void When_TruckNameContainSearchTerm() {
            String searchTerm = "바로우리";

            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<TruckList> result = truckListService.findTruckList(null, null, searchTerm, pageable);
            result.stream().forEach(r -> System.out.println(r.getName()));
            assertThat(result.get().findFirst().get().getName().contains(searchTerm));
        }

        @Test
        @Transactional
        @DisplayName("메뉴명에 검색어가 포함되어 있는 경우 해당 트럭 리스트 조회한다")
        void When_TruckMenuNameContainSearchTerm() {
            String searchTerm = "떡볶이";

            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<TruckList> result = truckListService.findTruckList(null, null, searchTerm, pageable);
            result.stream().forEach(r -> System.out.println(r.getName()));
            assertThat(result.get().findFirst().get().getMenuNames().contains(searchTerm));
        }
    }

    @Nested
    @Transactional
    @DisplayName("필터링 테스트")
    class Filtering {
        @BeforeEach
        void setUp() {
            Category category = Category.builder()
                    .name("중식")
                    .code("C01")
                    .build();
            category = categoryRepository.save(category);
            Category category1 = Category.builder()
                    .name("한식")
                    .code("C02")
                    .build();
            category1 = categoryRepository.save(category1);

            TruckCategory truckCategory = TruckCategory.builder()
                    .category(category)
                    .truck(truck)
                    .build();
            truckCategory = truckCategoryRepository.save(truckCategory);

            TruckRegion truckRegion = TruckRegion.builder()
                    .regionId(yongIn.getId())
                    .regionType(RegionType.REGION_SI)
                    .truck(truck)
                    .build();
            truckRegion = truckRegionRepository.save(truckRegion);
        }

        @Test
        @Transactional
        @DisplayName("필터를 걸지 않았을 때는 삭제되지 않은 트럭 전체가 조회되어야 한다")
        void When_NotFilter_Then_GetAllTruckList() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            assertThat(truckListService.findTruckList(null, null, null, pageable).get().noneMatch(truck -> truck.getId().equals(deletedTruck)));
        }

        @Test
        @Transactional
        @DisplayName("카테고리 필터링이 걸려있으면 헤당 카테고리에 속하는 트럭들이 조회되어야 한다")
        void When_CategoryFiltering() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<TruckList> result = truckListService.findTruckList(null, List.of("C01"), null, pageable);
            assertEquals(1, result.getNumberOfElements());
            assertEquals(truck.getName(), result.get().findFirst().get().getName());
        }

        @Test
        @Transactional
        @DisplayName("카테고리 필터링이 걸려있을 때 카테고리에 속하는 트럭이 없으면 0개가 반환되어야 한다")
        void When_NotExistCategoryFiltering() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<TruckList> result = truckListService.findTruckList(null, List.of("C02"), null, pageable);
            assertEquals(0, result.getNumberOfElements());
        }

        @Test
        @Transactional
        @DisplayName("지역 필터링이 걸려있으면 해당 지역에 속하는 트럭들이 조회되어야 한다")
        void When_RegionFiltering() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<TruckList> result = truckListService.findTruckList(List.of("SI" + yongIn.getId()), null, null, pageable);
            assertEquals(1, result.getNumberOfElements());
        }

        @Test
        @Transactional
        @DisplayName("도 지역 필터링이 걸려있으면 도에 속하는 시에 해당하는 트럭들이 조회되어야 한다")
        void When_RegionFiltering_DO() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<TruckList> result = truckListService.findTruckList(List.of("DO" + yongIn.getRegionDo().getId()), null, null, pageable);
            assertEquals(1, result.getNumberOfElements());
        }

        @Test
        @Transactional
        @DisplayName("시 지역 필터링이 걸리는 트럭이 없으면 0개 조회되어야 한다")
        void When_NotExistRegionFiltering_SI() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<TruckList> result = truckListService.findTruckList(List.of("SI" + anyang.getId()), null, null, pageable);
            assertEquals(0, result.getNumberOfElements());
        }
    }

    @Nested
    @DisplayName("정렬 테스트")
    class OrderBy {
        @Test
        @Transactional
        @DisplayName("정렬 기준이 최신순일 경우 푸드트럭 생성일자 기준으로 내림차순 정렬된다")
        void When_OrderByCreatedAt() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<TruckList> result = truckListService.findTruckList(null, null, null, pageable);
            assertEquals("푸드핀", result.stream().findFirst().get().getName());
            result.forEach(truck -> System.out.println(truck.getName()));
        }

        @Test
        @Transactional
        @DisplayName("정렬 기준이 조회순일 경우 푸드트럭 조회수 기준으로 내림차순 정렬된다")
        void When_OrderByViews() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "views"));
            Page<TruckList> result = truckListService.findTruckList(null, null, null, pageable);
            assertEquals("바로우리", result.stream().findFirst().get().getName());
            result.forEach(truck -> System.out.println(truck.getName()));
        }
    }

}
