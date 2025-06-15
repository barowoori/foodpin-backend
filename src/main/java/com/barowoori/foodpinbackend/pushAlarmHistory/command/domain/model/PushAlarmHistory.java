package com.barowoori.foodpinbackend.pushAlarmHistory.command.domain.model;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationTargetType;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "push_alarm_history")
@Getter
public class PushAlarmHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "members_id", nullable = false)
    private Member member;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "notification_target_type")
    private NotificationTargetType notificationTargetType;

    @Column(name = "target_id")
    private String targetId;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    protected PushAlarmHistory(){}

    @Builder
    public PushAlarmHistory(Member member, NotificationType notificationType, NotificationTargetType notificationTargetType, String targetId, String content) {
        this.member = member;
        this.notificationType = notificationType;
        this.notificationTargetType = notificationTargetType;
        this.targetId = targetId;
        this.content = content;
    }
}
