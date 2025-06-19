package com.barowoori.foodpinbackend.notification.command.application.service;

import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventApplicationRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventTruckRepository;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationTargetType;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationType;
import com.barowoori.foodpinbackend.notification.command.domain.model.truck.*;
import com.barowoori.foodpinbackend.notification.command.domain.service.NotificationService;
import com.barowoori.foodpinbackend.pushAlarmHistory.command.domain.model.PushAlarmHistory;
import com.barowoori.foodpinbackend.pushAlarmHistory.command.domain.repository.PushAlarmHistoryRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckManagerRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TruckNotificationEventHandler {
    private final NotificationService notificationService;
    private final EventTruckRepository eventTruckRepository;
    private final EventApplicationRepository eventApplicationRepository;
    private final TruckManagerRepository truckManagerRepository;
    private final MemberRepository memberRepository;
    private final PushAlarmHistoryRepository pushAlarmHistoryRepository;

    public TruckNotificationEventHandler(NotificationService notificationService,
                                         EventTruckRepository eventTruckRepository,
                                         EventApplicationRepository eventApplicationRepository,
                                         TruckManagerRepository truckManagerRepository,
                                         MemberRepository memberRepository,
                                         PushAlarmHistoryRepository pushAlarmHistoryRepository) {
        this.notificationService = notificationService;
        this.eventTruckRepository = eventTruckRepository;
        this.eventApplicationRepository = eventApplicationRepository;
        this.truckManagerRepository = truckManagerRepository;
        this.memberRepository = memberRepository;
        this.pushAlarmHistoryRepository = pushAlarmHistoryRepository;
    }

    private void savePushAlarmHistory(String memberId, NotificationType notificationType, NotificationTargetType notificationTargetType, String targetId, String content) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        PushAlarmHistory pushAlarmHistory = PushAlarmHistory.builder()
                .member(member)
                .notificationType(notificationType)
                .notificationTargetType(notificationTargetType)
                .targetId(targetId)
                .content(content)
                .build();
        pushAlarmHistoryRepository.save(pushAlarmHistory);
    }

    //선정 알림 Handler
    @EventListener(SelectionCompletedNotificationEvent.class)
    public void handle(SelectionCompletedNotificationEvent event) {
        NotificationType type = NotificationType.SELECTION_COMPLETED;
        NotificationTargetType targetType = NotificationTargetType.TRUCK_SELECTED_EVENT_LIST;

        String content = type.format(Map.of(
                "행사명", event.getEventName()
        ));
        List<MemberFcmInfoDto> memberFcmInfoDtos = eventTruckRepository.findEventTruckManagersFcmInfo(event.getEventTruckId());
        System.out.println("notificationMessage : " + content);
        memberFcmInfoDtos.forEach(memberFcmInfoDto -> {
            notificationService.pushAlarmToToken(type, targetType.name(), content, memberFcmInfoDto.getFcmToken(), targetType, event.getEventTruckId());

            savePushAlarmHistory(memberFcmInfoDto.getMemberId(), type, targetType, event.getEventTruckId(), content);
        });
    }

    //행사 모집 취소 Handler
    @EventListener(EventRecruitmentCanceledNotificationEvent.class)
    public void handle(EventRecruitmentCanceledNotificationEvent event) {
        NotificationType type = NotificationType.RECRUITMENT_CANCELED;
        NotificationTargetType targetType = NotificationTargetType.EVENT_DETAIL;

        String content = type.format(Map.of(
                "행사명", event.getEventName()
        ));
        List<MemberFcmInfoDto> memberFcmInfoDtos = eventApplicationRepository.findAllFcmInfoOfTruckManagersByEventId(event.getEventId());
        System.out.println("notificationMessage : " + content);
        memberFcmInfoDtos.forEach(memberFcmInfoDto -> {
            notificationService.pushAlarmToToken(type, targetType.name(), content, memberFcmInfoDto.getFcmToken(), targetType, event.getEventId());

            savePushAlarmHistory(memberFcmInfoDto.getMemberId(), type, targetType, event.getEventId(), content);
        });
    }

    //미선정 알림 Handler
    @EventListener(SelectionNotSelectedNotificationEvent.class)
    public void handle(SelectionNotSelectedNotificationEvent event) {
        NotificationType type = NotificationType.SELECTION_NOT_SELECTED;
        NotificationTargetType targetType = NotificationTargetType.NONE;

        String content = type.format(Map.of(
                "행사명", event.getEventName()
        ));
        List<MemberFcmInfoDto> memberFcmInfoDtos = eventApplicationRepository.findFcmInfoOfTruckManagers(event.getEventApplicationId());
        System.out.println("notificationMessage : " + content);
        memberFcmInfoDtos.forEach(memberFcmInfoDto -> {
            notificationService.pushAlarmToToken(type, targetType.name(), content, memberFcmInfoDto.getFcmToken(), targetType, null);

            savePushAlarmHistory(memberFcmInfoDto.getMemberId(), type, targetType, null, content);
        });
    }

    //선정 확정 알림 Handler
    @EventListener(TruckSelectionConfirmedNotificationEvent.class)
    public void handle(TruckSelectionConfirmedNotificationEvent event) {
        NotificationType type = NotificationType.TRUCK_SELECTION_CONFIRMED;
        NotificationTargetType targetType = NotificationTargetType.TRUCK_SELECTED_EVENT_LIST;

        String content = type.getTemplate();
        List<MemberFcmInfoDto> memberFcmInfoDtos = eventTruckRepository.findEventTruckManagersFcmInfo(event.getEventTruckId());
        System.out.println("notificationMessage : " + content);
        memberFcmInfoDtos.forEach(memberFcmInfoDto -> {
            notificationService.pushAlarmToToken(type, targetType.name(), content, memberFcmInfoDto.getFcmToken(), targetType, null);

            savePushAlarmHistory(memberFcmInfoDto.getMemberId(), type, targetType, null, content);
        });
    }

    //공지사항 알림 Handler
    @EventListener(EventNoticePostedNotificationEvent.class)
    public void handle(EventNoticePostedNotificationEvent event) {
        NotificationType type = NotificationType.EVENT_NOTICE_POSTED;
        NotificationTargetType targetType = NotificationTargetType.EVENT_NOTICE_DETAIL;

        String content = type.format(Map.of(
                "행사명", event.getEventName()
        ));

        List<MemberFcmInfoDto> memberFcmInfoDtos = eventTruckRepository.findConfirmedEventTruckManagersFcmInfo(event.getEventId());
        System.out.println("notificationMessage : " + content);
        memberFcmInfoDtos.forEach(memberFcmInfoDto -> {
            notificationService.pushAlarmToToken(type, targetType.name(), content, memberFcmInfoDto.getFcmToken(), targetType, event.getNoticeId());

            savePushAlarmHistory(memberFcmInfoDto.getMemberId(), type, targetType, event.getNoticeId(), content);
        });
    }

    //행사 섭외 알림 Handler
    @EventListener(EventCastedNotificationEvent.class)
    public void handle(EventCastedNotificationEvent event) {
        NotificationType type = NotificationType.EVENT_CASTED;
        NotificationTargetType targetType = NotificationTargetType.EVENT_DETAIL;

        String content = type.format(Map.of(
                "행사명", event.getEventName()
        ));

        List<MemberFcmInfoDto> memberFcmInfoDtos = truckManagerRepository.findTruckManagersFcmInfo(event.getEventId());
        System.out.println("notificationMessage : " + content);
        memberFcmInfoDtos.forEach(memberFcmInfoDto -> {
            notificationService.pushAlarmToToken(type, targetType.name(), content, memberFcmInfoDto.getFcmToken(), targetType, event.getEventId());

            savePushAlarmHistory(memberFcmInfoDto.getMemberId(), type, targetType, event.getEventId(), content);
        });
    }

    //운영자 추가 알림 Handler
    @EventListener(ManagerAddedNotificationEvent.class)
    public void handle(ManagerAddedNotificationEvent event) {
        NotificationType type = NotificationType.MANAGER_ADDED;
        NotificationTargetType targetType = NotificationTargetType.TRUCK_MANAGER_LIST;

        String content = type.format(Map.of(
                "푸드트럭명", event.getTruckName(),
                "닉네임", event.getNickname()
        ));

        List<MemberFcmInfoDto> memberFcmInfoDtos = truckManagerRepository.findTruckManagersFcmInfo(event.getTruckId());
        System.out.println("notificationMessage : " + content);
        memberFcmInfoDtos.forEach(memberFcmInfoDto -> {
            notificationService.pushAlarmToToken(type, targetType.name(), content, memberFcmInfoDto.getFcmToken(), targetType, event.getTruckId());

            savePushAlarmHistory(memberFcmInfoDto.getMemberId(), type, targetType, event.getTruckId(), content);
        });
    }

    //운영자 삭제 알림 Handler
    @EventListener(ManagerRemovedNotificationEvent.class)
    public void handle(ManagerRemovedNotificationEvent event) {
        NotificationType type = NotificationType.MANAGER_REMOVED;
        NotificationTargetType targetType = NotificationTargetType.NONE;

        String content = type.format(Map.of(
                "푸드트럭명", event.getTruckName()
        ));

        MemberFcmInfoDto memberFcmInfoDto = memberRepository.findMemberFcmInfo(event.getMemberId());
        notificationService.pushAlarmToToken(type, targetType.name(), content, memberFcmInfoDto.getFcmToken(), targetType, null);

        savePushAlarmHistory(memberFcmInfoDto.getMemberId(), type, targetType, null, content);
    }

    //소유자 변경 알림 Handler
    @EventListener(OwnerUpdatedNotificationEvent.class)
    public void handle(OwnerUpdatedNotificationEvent event) {
        NotificationType type = NotificationType.OWNER_UPDATED;
        NotificationTargetType targetType = NotificationTargetType.TRUCK_MANAGER_LIST;

        String content = type.format(Map.of(
                "푸드트럭명", event.getTruckName(),
                "닉네임", event.getNickname()
        ));

        List<MemberFcmInfoDto> memberFcmInfoDtos = truckManagerRepository.findTruckManagersFcmInfo(event.getTruckId());
        System.out.println("notificationMessage : " + content);
        memberFcmInfoDtos.forEach(memberFcmInfoDto -> {
            notificationService.pushAlarmToToken(type, targetType.name(), content, memberFcmInfoDto.getFcmToken(), targetType, event.getTruckId());

            savePushAlarmHistory(memberFcmInfoDto.getMemberId(), type, targetType, event.getTruckId(), content);
        });
    }
}
