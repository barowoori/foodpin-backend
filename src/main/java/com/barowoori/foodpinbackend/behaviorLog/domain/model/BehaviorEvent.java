package com.barowoori.foodpinbackend.behaviorLog.domain.model;

public enum BehaviorEvent {
    // 백엔드 자동 적재
    UNREG_REGISTER,                       // 비회원가입(registerTemporary) 시점
    UNREG_SIGNUP,                         // 비회원 → 정회원 가입 완료(registerMember) 시점

    // 프론트 직접 호출(향후 BehaviorLogController에서 사용)
    UNREG_HOME_TRUCK_REGISTER,
    UNREG_HOME_EVENT_REGISTER,
    UNREG_HOME_NOTIFICATION,
    UNREG_TRUCK_LIST_MY_TRUCK_REGISTER,
    UNREG_TRUCK_DETAIL_HIRE,
    UNREG_TRUCK_DETAIL_LIKE,
    UNREG_EVENT_LIST_MY_EVENT_REGISTER,
    UNREG_EVENT_LIST_NOTIFICATION,
    UNREG_EVENT_DETAIL_APPLY,
    UNREG_EVENT_DETAIL_LIKE,
    UNREG_MY_PAGE
}
