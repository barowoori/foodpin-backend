package com.barowoori.foodpinbackend.event.application.service;

import com.barowoori.foodpinbackend.event.command.application.service.EventPushNotificator;
import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventDocumentSubmissionTarget;
import com.barowoori.foodpinbackend.event.command.domain.model.EventTruck;
import com.barowoori.foodpinbackend.event.command.domain.model.EventTruckStatus;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventTruckRepository;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManager;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManagerRole;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckManagerRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class EventPushNotificatorTests {
    @Autowired
    EntityManager em;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EventPushNotificator eventPushNotificator;
    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private TruckManagerRepository truckManagerRepository;
    @Autowired
    private EventTruckRepository eventTruckRepository;

    private EventTruck eventTruck;

    @BeforeEach
    void init() {
        Event event = saveBasicEvent();

        Truck truck = Truck.builder()
                .name("바로우리")
                .isDeleted(Boolean.FALSE)
                .build();
        truck = truckRepository.save(truck);

        Member member = memberRepository.save(Member.builder()
                .email("email")
                .phone("01012341234")
                .nickname("truckMangerMember")
                .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                .build());

        TruckManager truckManager = truckManagerRepository.save(TruckManager.builder()
                .role(TruckManagerRole.MEMBER)
                .member(member)
                .truck(truck)
                .build());

        eventTruck = EventTruck.builder()
                .event(event)
                .truck(truck)
                .status(EventTruckStatus.PENDING)
                .build();
        eventTruck = eventTruckRepository.save(eventTruck);
    }

    private void setCreatedAtManually(EventTruck eventTruck, LocalDateTime newCreatedAt) {
        // 필드 접근을 허용하기 위해 리플렉션 사용
        try {
            Field field = EventTruck.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(eventTruck, newCreatedAt);
        } catch (Exception e) {
            throw new RuntimeException("createdAt 설정 실패", e);
        }
    }


    @DisplayName("회신 요청 알림 테스트")
    @Nested
    class ReplyRequestNotificationTest {

        @Test
        @DisplayName("24시간 미만 경과 시 알림이 전송되지 않는다")
        void doesNotSendNotificationIfLessThan24HoursPassed() {
            PrintStream originalOut = System.out;

            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            try {
                setCreatedAtManually(eventTruck, LocalDateTime.now().minusHours(20));
                em.flush();
                em.clear();
                eventPushNotificator.sendRepeatedReplyRequestPushNotification();

                String output = outContent.toString();

                assertFalse(output.contains("참여 가능하신가요?"));
                assertFalse(output.contains("서둘러 참여 여부를 알려주세요."));
            } finally {
                // 원래 출력으로 복원
                System.setOut(originalOut);
            }
        }

        @Test
        @DisplayName("정확히 24시간 경과 시 알림이 전송된다")
        void sendsNotificationExactlyAt24Hours() {
            PrintStream originalOut = System.out;

            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            try {
                setCreatedAtManually(eventTruck, LocalDateTime.now().minusHours(24));
                em.flush();
                em.clear();
                eventPushNotificator.sendRepeatedReplyRequestPushNotification();

                String output = outContent.toString();

                assertTrue(output.contains("참여 가능하신가요?"));
                assertTrue(output.contains("서둘러 참여 여부를 알려주세요."));
            } finally {
                // 원래 출력으로 복원
                System.setOut(originalOut);
            }
        }

        @Test
        @DisplayName("48시간 경과 시 반복 알림이 전송된다")
        void sendsRepeatedNotificationAt48Hours() {
            PrintStream originalOut = System.out;

            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            try {
                setCreatedAtManually(eventTruck, LocalDateTime.now().minusHours(48));
                em.flush();
                em.clear();
                eventPushNotificator.sendRepeatedReplyRequestPushNotification();

                String output = outContent.toString();

                assertTrue(output.contains("참여 가능하신가요?"));
                assertTrue(output.contains("서둘러 참여 여부를 알려주세요."));
            } finally {
                // 원래 출력으로 복원
                System.setOut(originalOut);
            }
        }

    }

    private Event saveBasicEvent() {
        Member memberBuilder = Member.builder()
                .email("email")
                .phone("01012341234")
                .nickname("nickname")
                .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                .build();
        Member member = memberRepository.save(memberBuilder);
        return eventRepository.save(Event.builder()
                .name("스케쥴러 테스트 이벤트")
                .createdBy(member.getId())
                .description("desc")
                .guidelines("guideline")
                .isDeleted(false)
                .submissionEmail("test@example.com")
                .documentSubmissionTarget(EventDocumentSubmissionTarget.ALL_APPLICANTS)
                .build());
    }
}
