package com.barowoori.foodpinbackend.statistics.command.domain.repository;

import com.barowoori.foodpinbackend.statistics.command.domain.model.DailySignupStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailySignupStatisticsRepository extends JpaRepository<DailySignupStatistics, String> {
    Optional<DailySignupStatistics> findByStatDate(LocalDate statDate);
    List<DailySignupStatistics> findAllByStatDateBetweenOrderByStatDateAsc(LocalDate from, LocalDate to);
}
