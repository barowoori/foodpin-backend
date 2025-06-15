package com.barowoori.foodpinbackend.pushAlarmHistory.command.domain.repository;

import com.barowoori.foodpinbackend.pushAlarmHistory.command.domain.model.PushAlarmHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushAlarmHistoryRepository extends JpaRepository<PushAlarmHistory, String> {
}
