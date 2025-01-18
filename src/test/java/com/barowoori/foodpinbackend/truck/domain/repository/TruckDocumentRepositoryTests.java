package com.barowoori.foodpinbackend.truck.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckDocumentRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDocumentManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TruckDocumentRepositoryTests {
    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private TruckDocumentRepository truckDocumentRepository;

    Truck truck;

    @BeforeEach
    void setUp() {
        truck = Truck.builder()
                .name("바로우리")
                .description("바로우리 트럭입니다")
                .isDeleted(Boolean.FALSE)
                .build();
        truck = truckRepository.save(truck);
    }

    @Nested
    @DisplayName("트럭 아이디로 트럭 서류 관리 객체를 조회한다")
    class GetTruckDocumentManager {
        @BeforeEach
        void setUp(){
            TruckDocument truckDocument = TruckDocument.builder()
                    .type(DocumentType.BUSINESS_LICENSE)
                    .path("path")
                    .truck(truck)
                    .build();
            truckDocument = truckDocumentRepository.save(truckDocument);

            TruckDocument truckDocument1 = TruckDocument.builder()
                    .type(DocumentType.BUSINESS_LICENSE)
                    .path("path")
                    .truck(truck)
                    .build();
            truckDocument1 = truckDocumentRepository.save(truckDocument1);
        }

        @Test
        @DisplayName("타입에 해당하는 서류 리스트를 반환한다")
        void getDocumentsByType() {
            TruckDocumentManager documentManager = truckDocumentRepository.getDocumentManager(truck.getId());
            assertEquals(2, documentManager.getDocumentsByType(DocumentType.BUSINESS_LICENSE).size());
        }

        @Test
        @DisplayName("타입에 해당하는 서류 존재 여부를 반환한다")
        void hasDocumentsByType() {
            TruckDocumentManager documentManager = truckDocumentRepository.getDocumentManager(truck.getId());
            assertTrue(documentManager.hasDocumentType(DocumentType.BUSINESS_LICENSE));
            assertFalse(documentManager.hasDocumentType(DocumentType.BUSINESS_REGISTRATION));
        }

        @Test
        @DisplayName("타입 리스트는 중복되지 않게 존재하는 것만 반환한다")
        void getType() {
            TruckDocumentManager documentManager = truckDocumentRepository.getDocumentManager(truck.getId());
            assertEquals(1, documentManager.getTypes().size());
        }
    }

    @Nested
    @DisplayName("서류가 아예 없을 경우에도 documentManager 반환한다")
    class GetTruckDocumentManagerWhenNotExistDocuments{
        @Test
        @DisplayName("타입에 해당하는 서류 존재 여부는 항상 false를 반환한다")
        void hasDocumentsByType(){
            TruckDocumentManager documentManager = truckDocumentRepository.getDocumentManager(truck.getId());
            assertFalse(documentManager.hasDocumentType(DocumentType.BUSINESS_REGISTRATION));
            assertFalse(documentManager.hasDocumentType(DocumentType.BUSINESS_LICENSE));
            assertFalse(documentManager.hasDocumentType(DocumentType.SANITATION_EDUCATION));
        }

        @Test
        @DisplayName("타입에 해당하는 서류 리스트는 항상 빈 리스트로 반환한다")
        void getDocumentsByType() {
            TruckDocumentManager documentManager = truckDocumentRepository.getDocumentManager(truck.getId());
            assertEquals(0, documentManager.getDocumentsByType(DocumentType.BUSINESS_LICENSE).size());
        }

        @Test
        @DisplayName("타입 리스트는 항상 빈 리스트로 반환한다")
        void getType() {
            TruckDocumentManager documentManager = truckDocumentRepository.getDocumentManager(truck.getId());
            assertEquals(0, documentManager.getTypes().size());
        }

    }

}
