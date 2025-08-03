package com.barowoori.foodpinbackend.pushAlarmHistory.command.application.service;

import com.barowoori.foodpinbackend.pushAlarmHistory.command.application.dto.ResponsePushAlarmHistory;
import com.barowoori.foodpinbackend.pushAlarmHistory.command.domain.model.PushAlarmHistory;
import com.barowoori.foodpinbackend.pushAlarmHistory.command.domain.repository.PushAlarmHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PushAlarmHistoryService {
    private final PushAlarmHistoryRepository pushAlarmHistoryRepository;

    public PushAlarmHistoryService(PushAlarmHistoryRepository pushAlarmHistoryRepository) {
        this.pushAlarmHistoryRepository = pushAlarmHistoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<ResponsePushAlarmHistory.GetPushAlarmHistory> getPushAlarmHistories(String memberId, Pageable pageable) {
        Page<PushAlarmHistory> pushAlarmHistories = pushAlarmHistoryRepository.findPushAlarmHistoryByMemberId(memberId,pageable);
        return pushAlarmHistories.map(ResponsePushAlarmHistory.GetPushAlarmHistory::of);
    }

}
