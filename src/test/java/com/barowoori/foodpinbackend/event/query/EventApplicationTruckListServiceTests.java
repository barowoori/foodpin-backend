package com.barowoori.foodpinbackend.event.query;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventDocument;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventDocumentRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventApplicableTruckList;
import com.barowoori.foodpinbackend.event.query.application.EventApplicableTruckListService;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManager;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManagerRole;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckDocumentRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckManagerRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class EventApplicationTruckListServiceTests {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventDocumentRepository eventDocumentRepository;
    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private TruckManagerRepository truckManagerRepository;
    @Autowired
    private EventApplicableTruckListService eventApplicableTruckListService;
    @Autowired
    private TruckDocumentRepository truckDocumentRepository;

    Member member;
    Event event;
    Truck truck;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("email")
                .phone("01012341234")
                .nickname("nickname")
                .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                .build();
        member = memberRepository.save(member);

        event = Event.builder()
                .createdBy("user")
                .name("2월 행사")
                .isDeleted(Boolean.FALSE)
                .build();
        event = eventRepository.save(event);


        truck = Truck.builder()
                .name("바로우리")
                .description("바로우리 트럭입니다")
                .isDeleted(Boolean.FALSE)
                .build();
        truck = truckRepository.save(truck);

        TruckManager truckManager = TruckManager.builder()
                .member(member)
                .role(TruckManagerRole.MEMBER)
                .truck(truck)
                .build();
        truckManager = truckManagerRepository.save(truckManager);

        TruckDocument truckDocument = TruckDocument.builder()
                .truck(truck)
                .type(DocumentType.BUSINESS_REGISTRATION)
                .build();
        truckDocument = truckDocumentRepository.save(truckDocument);

    }

    private EventDocument createEventDocument(Event event, DocumentType documentType) {
        EventDocument eventDocument = EventDocument.builder()
                .event(event)
                .type(documentType)
                .build();
        return eventDocumentRepository.save(eventDocument);
    }

    @Test
    @Transactional
    @DisplayName("가지고 있는 트럭이 없을 경우 조회되지 않는다")
    void When_NotHaveTruck_Then_EmptyList() {
        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        Page<EventApplicableTruckList> result = eventApplicableTruckListService.findApplicableTrucks(event.getId(), "test", pageable);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("행사에 필요한 서류가 없을 경우 해당 서류 리스트를 반환한다")
    void When_MissingDocumentExist_Then_ReturnThatList() {
        EventDocument businessLicense = createEventDocument(event, DocumentType.BUSINESS_LICENSE);
        EventDocument businessRegistration = createEventDocument(event, DocumentType.BUSINESS_REGISTRATION);

        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        Page<EventApplicableTruckList> result = eventApplicableTruckListService.findApplicableTrucks(event.getId(), member.getId(), pageable);
        assertEquals(1, result.stream().findFirst().get().getMissingDocuments().size());
        System.out.println(result.stream().findFirst().get().getMissingDocuments());
    }


}
