package com.barowoori.foodpinbackend.statistics.command.application.service;

import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.member.command.domain.repository.UnregisteredMemberRedisRepository;
import com.barowoori.foodpinbackend.statistics.command.domain.model.DailySignupStatistics;
import com.barowoori.foodpinbackend.statistics.command.domain.repository.DailySignupStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class DailySignupStatisticsBatchService {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    private final MemberRepository memberRepository;
    private final UnregisteredMemberRedisRepository unregisteredMemberRedisRepository;
    private final DailySignupStatisticsRepository dailySignupStatisticsRepository;

    @Scheduled(cron = "0 10 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void aggregateYesterdaySignupStatistics() {
        upsertDailyStatistics(LocalDate.now(ZONE_ID).minusDays(1));
    }

    @Transactional
    public void upsertDailyStatistics(LocalDate statDate) {
        LocalDateTime start = statDate.atStartOfDay();
        LocalDateTime end = statDate.atTime(LocalTime.MAX);

        long memberSignupCount = memberRepository.countByCreatedAtBetweenAndSocialLoginInfo_TypeNot(start, end, SocialLoginType.UNREGISTERED);
        long guestSignupCount = unregisteredMemberRedisRepository.countRegisteredBetween(start, end);

        DailySignupStatistics statistics = dailySignupStatisticsRepository.findByStatDate(statDate)
                .orElseGet(() -> DailySignupStatistics.builder()
                        .statDate(statDate)
                        .memberSignupCount(0L)
                        .guestSignupCount(0L)
                        .totalSignupCount(0L)
                        .build());

        statistics.updateCounts(memberSignupCount, guestSignupCount);
        dailySignupStatisticsRepository.save(statistics);
    }
}
