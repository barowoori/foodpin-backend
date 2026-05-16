package com.barowoori.foodpinbackend.statistics.query.application;

import com.barowoori.foodpinbackend.common.exception.CommonErrorCode;
import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.member.command.application.dto.ResponseBackOfficeMember;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.member.command.domain.repository.UnregisteredMemberRedisRepository;
import com.barowoori.foodpinbackend.statistics.command.domain.model.DailySignupStatistics;
import com.barowoori.foodpinbackend.statistics.command.domain.repository.DailySignupStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BackOfficeSignupStatisticsService {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    private final DailySignupStatisticsRepository dailySignupStatisticsRepository;
    private final MemberRepository memberRepository;
    private final UnregisteredMemberRedisRepository unregisteredMemberRedisRepository;

    @Transactional(readOnly = true)
    public ResponseBackOfficeMember.BackOfficeSignupStatisticsDto getSignupStatistics(LocalDate from, LocalDate to, ResponseBackOfficeMember.SignupStatisticsUnit unit) {
        validate(from, to, unit);

        List<DailySignupStatistics> statistics = dailySignupStatisticsRepository.findAllByStatDateBetweenOrderByStatDateAsc(from, to);
        Map<LocalDate, DailySignupStatistics> statisticsByDate = new LinkedHashMap<>();
        for (DailySignupStatistics stat : statistics) {
            statisticsByDate.put(stat.getStatDate(), stat);
        }

        List<ResponseBackOfficeMember.SignupStatisticsBucketDto> buckets = switch (unit) {
            case DAY -> buildDailyBuckets(from, to, statisticsByDate);
            case WEEK -> buildWeeklyBuckets(from, to, statisticsByDate);
            case MONTH -> buildMonthlyBuckets(from, to, statisticsByDate);
        };

        long memberTotal = buckets.stream().mapToLong(ResponseBackOfficeMember.SignupStatisticsBucketDto::getMemberSignupCount).sum();
        long guestTotal = buckets.stream().mapToLong(ResponseBackOfficeMember.SignupStatisticsBucketDto::getGuestSignupCount).sum();

        return ResponseBackOfficeMember.BackOfficeSignupStatisticsDto.builder()
                .from(from)
                .to(to)
                .unit(unit)
                .summary(ResponseBackOfficeMember.SignupStatisticsSummaryDto.builder()
                        .memberSignupCount(memberTotal)
                        .guestSignupCount(guestTotal)
                        .totalSignupCount(memberTotal + guestTotal)
                        .build())
                .buckets(buckets)
                .build();
    }

    @Transactional(readOnly = true)
    public ResponseBackOfficeMember.TodaySignupStatisticsDto getTodaySignupStatistics() {
        LocalDate today = LocalDate.now(ZONE_ID);
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        long memberSignupCount = memberRepository.countByCreatedAtBetweenAndSocialLoginInfo_TypeNot(start, end, SocialLoginType.UNREGISTERED);
        long guestSignupCount = unregisteredMemberRedisRepository.countRegisteredBetween(start, end)
                + memberRepository.countByCreatedAtBetweenAndSocialLoginInfo_Type(start, end, SocialLoginType.UNREGISTERED);

        return ResponseBackOfficeMember.TodaySignupStatisticsDto.builder()
                .date(today)
                .memberSignupCount(memberSignupCount)
                .guestSignupCount(guestSignupCount)
                .totalSignupCount(memberSignupCount + guestSignupCount)
                .build();
    }

    private List<ResponseBackOfficeMember.SignupStatisticsBucketDto> buildDailyBuckets(LocalDate from, LocalDate to, Map<LocalDate, DailySignupStatistics> statisticsByDate) {
        List<ResponseBackOfficeMember.SignupStatisticsBucketDto> buckets = new ArrayList<>();
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            buckets.add(toBucket(date, date, statisticsByDate));
        }
        return buckets;
    }

    private List<ResponseBackOfficeMember.SignupStatisticsBucketDto> buildWeeklyBuckets(LocalDate from, LocalDate to, Map<LocalDate, DailySignupStatistics> statisticsByDate) {
        List<ResponseBackOfficeMember.SignupStatisticsBucketDto> buckets = new ArrayList<>();
        LocalDate cursor = from.with(DayOfWeek.MONDAY);
        while (!cursor.isAfter(to)) {
            LocalDate startDate = cursor.isBefore(from) ? from : cursor;
            LocalDate endDate = cursor.plusDays(6);
            if (endDate.isAfter(to)) {
                endDate = to;
            }
            buckets.add(toBucket(startDate, endDate, statisticsByDate));
            cursor = cursor.plusWeeks(1);
        }
        return buckets;
    }

    private List<ResponseBackOfficeMember.SignupStatisticsBucketDto> buildMonthlyBuckets(LocalDate from, LocalDate to, Map<LocalDate, DailySignupStatistics> statisticsByDate) {
        List<ResponseBackOfficeMember.SignupStatisticsBucketDto> buckets = new ArrayList<>();
        LocalDate cursor = from.withDayOfMonth(1);
        while (!cursor.isAfter(to)) {
            LocalDate startDate = cursor.isBefore(from) ? from : cursor;
            LocalDate endDate = cursor.withDayOfMonth(cursor.lengthOfMonth());
            if (endDate.isAfter(to)) {
                endDate = to;
            }
            buckets.add(toBucket(startDate, endDate, statisticsByDate));
            cursor = cursor.plusMonths(1).withDayOfMonth(1);
        }
        return buckets;
    }

    private ResponseBackOfficeMember.SignupStatisticsBucketDto toBucket(LocalDate startDate, LocalDate endDate, Map<LocalDate, DailySignupStatistics> statisticsByDate) {
        long memberCount = 0L;
        long guestCount = 0L;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DailySignupStatistics statistics = statisticsByDate.get(date);
            if (statistics == null) {
                continue;
            }
            memberCount += statistics.getMemberSignupCount();
            guestCount += statistics.getGuestSignupCount();
        }

        return ResponseBackOfficeMember.SignupStatisticsBucketDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .memberSignupCount(memberCount)
                .guestSignupCount(guestCount)
                .totalSignupCount(memberCount + guestCount)
                .build();
    }

    private void validate(LocalDate from, LocalDate to, ResponseBackOfficeMember.SignupStatisticsUnit unit) {
        if (from == null || to == null || unit == null) {
            throw new CustomException(CommonErrorCode.EMPTY_PARAMETER);
        }
        if (from.isAfter(to)) {
            throw new CustomException(CommonErrorCode.EMPTY_PARAMETER);
        }
    }
}
