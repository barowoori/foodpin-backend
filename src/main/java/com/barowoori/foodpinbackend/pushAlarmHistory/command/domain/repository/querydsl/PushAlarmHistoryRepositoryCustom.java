package com.barowoori.foodpinbackend.pushAlarmHistory.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.pushAlarmHistory.command.domain.model.PushAlarmHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PushAlarmHistoryRepositoryCustom {
    Page<PushAlarmHistory> findPushAlarmHistoryByMemberId(String memberId, Pageable pageable);
}
