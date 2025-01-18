package com.barowoori.foodpinbackend.truck.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckPhoto;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckPhotoRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class TruckPhotoRepositoryTests {
    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private TruckPhotoRepository truckPhotoRepository;

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
            TruckPhoto truckPhoto = TruckPhoto.builder()
                    .path("path" + i)
                    .truck(truck)
                    .build();
            truckPhotoRepository.save(truckPhoto);
        }
    }

    @Test
    @DisplayName("트럭 이미지를 조회할 때 등록된 순서대로 조회되어야 한다")
    void WhenFindByTruckThenOrderByCreateAt() {
        List<TruckPhoto> photos = truckPhotoRepository.findByTruckOrderByCreateAt(truck);
        for (int i = 0; i < photos.size() - 1; i++) {
            assertTrue(photos.get(i).getCreateAt().isBefore(photos.get(i + 1).getCreateAt()));
        }
    }
}
