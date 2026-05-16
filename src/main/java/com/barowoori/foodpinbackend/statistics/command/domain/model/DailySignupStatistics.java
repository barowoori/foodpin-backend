package com.barowoori.foodpinbackend.statistics.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_signup_statistics", uniqueConstraints = {
        @UniqueConstraint(name = "uk_daily_signup_statistics_stat_date", columnNames = "stat_date")
})
@Getter
public class DailySignupStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "stat_date", nullable = false, unique = true)
    private LocalDate statDate;

    @Column(name = "member_signup_count", nullable = false)
    private Long memberSignupCount;

    @Column(name = "guest_signup_count", nullable = false)
    private Long guestSignupCount;

    @Column(name = "total_signup_count", nullable = false)
    private Long totalSignupCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected DailySignupStatistics() {
    }

    @Builder
    public DailySignupStatistics(LocalDate statDate, Long memberSignupCount, Long guestSignupCount, Long totalSignupCount) {
        this.statDate = statDate;
        this.memberSignupCount = memberSignupCount;
        this.guestSignupCount = guestSignupCount;
        this.totalSignupCount = totalSignupCount;
    }

    public void updateCounts(long memberSignupCount, long guestSignupCount) {
        this.memberSignupCount = memberSignupCount;
        this.guestSignupCount = guestSignupCount;
        this.totalSignupCount = memberSignupCount + guestSignupCount;
    }
}
