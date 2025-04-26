package com.barowoori.foodpinbackend.notification.command.domain.model;

import lombok.Getter;

import java.util.Map;
@Getter
public enum NotificationType {
    // 지원/선정 관련
    APPLICATION_RECEIVED("지원자 알림", "[{행사명}] 새로운 지원자를 확인해 주세요!"),
    SELECTION_CONFIRMED("선정 확정 알림", "[{행사명}] {푸드트럭명}님의 참여가 확정되었습니다."),
    TRUCK_SELECTION_CONFIRMED("선정 확정 알림", "참여가 확정되었습니다. \n" +
            "공지사항을 확인해 주세요!"),
    SELECTION_CANCELED("선정 취소 알림", "[{행사명}] {푸드트럭명}님의 참여 불가로, 선정이 취소되었습니다.\n" +
            "새로운 푸드트럭을 선정해 주세요."),
    SELECTION_NOT_SELECTED("미선정 알림","아쉽게도, {행사명}에 선정되지 않았어요."),
    SELECTION_COMPLETED("선정 알림", "지원하신 행사에 선정되었습니다.\n" +
            "{행사명}\n" +
            "3일 이내에 참석여부 답변 후 확정됩니다!"),
    SELECTION_DEADLINE_SOON("선정마감일 알림", "[{행사명}] 선정 마감 6시간 전입니다.\n" +
            "서둘러 푸드트럭을 선정해 주세요!"),
    ANSWER_DEADLINE_SOON("답변 마감임박 알림","지원자님의 참여 답변을 기다리고 있어요.\n" +
            "오늘까지 확정하지 않으면, 선정이 자동 취소됩니다."),

    // 모집 관련
    RECRUITMENT_DEADLINE_SOON("모집마감일 알림", "[{행사명}] 모집 마감 6시간 전입니다.\n" +
            "추가 모집이 필요하다면 모집마감일을 연장해 주세요!"),
    RECRUITMENT_CANCELED("모집취소 알림","[{행사명}] 모집이 취소되었어요."),

    // 관심 행사 관련
    INTEREST_REGISTERED("관심 행사 등록 알림", "{행사지역} - {행사명}"),
    INTEREST_DEADLINE_SOON("관심 행사 마감일 알림","관심 설정한 행사가 6시간 후 모집 마감됩니다.\n" +
            "서둘러 지원해 주세요!\n" +
            "{행사지역} - {행사명}"),

    // 공지
    EVENT_NOTICE_POSTED("공지사항 알림", "[{행사명}] 새로운 공지사항이 등록되었습니다."),

    // 섭외 관련
    EVENT_CASTED("행사 섭외 알림","[{행사명}] 행사 참여 제안이 도착했습니다."),

    // 운영자 관련
    MANAGER_ADDED("운영자 추가 알림", "{푸드트럭명} 운영자로 {닉네임}님이 추가되었습니다."),
    MANAGER_REMOVED("운영자 삭제 알림","{푸드트럭명} 운영자 권한이 삭제되었습니다."),
    OWNER_UPDATED("소유자 변경 알림","{푸드트럭} 소유자가 {닉네임}님으로 변경되었습니다.");

    private final String name;
    private final String template;

    NotificationType(String name, String template) {
        this.name = name;
        this.template = template;
    }

    public String format(Map<String, String> args) {
        String msg = template;
        for (Map.Entry<String, String> entry : args.entrySet()) {
            msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return msg;
    }
}
