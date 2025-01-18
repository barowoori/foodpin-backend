package com.barowoori.foodpinbackend.truck.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenu;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenuPhoto;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckMenuPhotoRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckMenuRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class TruckMenuRepositoryTests {
    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private TruckMenuRepository truckMenuRepository;
    @Autowired
    private TruckMenuPhotoRepository truckMenuPhotoRepository;

    Truck truck;

    @BeforeEach
    void setUp() {
        truck = Truck.builder()
                .name("바로우리")
                .description("바로우리 트럭입니다")
                .isDeleted(Boolean.FALSE)
                .build();
        truck = truckRepository.save(truck);

        for (int i = 0; i < 5; i++) {
            TruckMenu truckMenu = TruckMenu.builder()
                    .name("떡볶이" + i)
                    .description("맛있다")
                    .price(1000)
                    .truck(truck)
                    .build();
            truckMenuRepository.save(truckMenu);
            for (int j = 0; j < 5; j++) {
                TruckMenuPhoto truckMenuPhoto = TruckMenuPhoto.builder()
                        .truckMenu(truckMenu)
                        .path("path"+ j)
                        .build();
                truckMenuPhotoRepository.save(truckMenuPhoto);
            }
        }
    }
    @Nested
    @DisplayName("메뉴 목록 조회")
    class GetTruckMenuSummary{
        @Test
        @DisplayName("메뉴와 그 메뉴의 사진들을 리스트로 가져온다")
        void then_exist_menu_photo_list(){
            List<TruckMenu> menus = truckMenuRepository.getMenuListWithPhotoByTruckId(truck.getId());
            assertEquals(5, menus.size());
            for (TruckMenu menu : menus){
                assertEquals(5, menu.getPhotos().size());
            }
        }
    }



}
