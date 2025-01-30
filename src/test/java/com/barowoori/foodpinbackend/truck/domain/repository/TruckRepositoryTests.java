package com.barowoori.foodpinbackend.truck.domain.repository;

import com.barowoori.foodpinbackend.file.domain.model.File;
import com.barowoori.foodpinbackend.file.domain.repository.FileRepository;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckPhoto;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckPhotoRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class TruckRepositoryTests {
    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private TruckPhotoRepository truckPhotoRepository;

    @Autowired
    private FileRepository fileRepository;
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
            File file = fileRepository.save(File.builder().path("path" + i).build());
            TruckPhoto truckPhoto = TruckPhoto.builder()
                    .file(file)
                    .truck(truck)
                    .build();
            truckPhotoRepository.save(truckPhoto);
        }
    }

    @Nested
    @DisplayName("트럭 사진과 함께 조회")
    class GetTruckWithPhotos{
        @Test
        @DisplayName("트럭 정보와 사진 리스트가 함께 조회되어야 한다")
        void Then_withPhotoLists(){
            Truck resultTruck = truckRepository.getTruckWithPhotoById(truck.getId());
            assertNotNull(resultTruck);
            assertEquals(5, resultTruck.getPhotos().size());
        }
    }
}
