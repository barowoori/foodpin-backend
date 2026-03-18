package com.barowoori.foodpinbackend.event.command.domain.model;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.member.command.domain.model.EventCreatorType;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckType;
import com.barowoori.foodpinbackend.truck.command.domain.service.TruckTypeSetConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "events")
@Getter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "creator_type")
    private EventCreatorType creatorType;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "guidelines", columnDefinition = "TEXT")
    private String guidelines;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "document_submission_target")
    private EventDocumentSubmissionTarget documentSubmissionTarget;

    @Column(name = "submission_email")
    private String submissionEmail;

    @Column(name = "status_comment")
    private String statusComment;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "is_hidden", nullable = false)
    private boolean isHidden = false;

    @OneToOne(mappedBy = "event")
    private EventView view;

    @OneToOne(mappedBy = "event")
    private EventRecruitDetail recruitDetail;

    @OneToOne(mappedBy = "event")
    private EventRegion eventRegion;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    private EventType type;

    @Column(name = "recruitment_url", length = 500)
    private String recruitmentUrl;

    @Column(name = "recruitment_url_click_count", nullable = false)
    private int recruitmentUrlClickCount = 0;

    @Column(name = "truck_types")
    @Convert(converter = TruckTypeSetConverter.class)
    private Set<TruckType> truckTypes = new HashSet<>();

    @Enumerated(value = EnumType.STRING)
    @Column(name = "expected_participants")
    private ExpectedParticipants expectedParticipants;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_type")
    private SaleType saleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_range")
    private PriceRange priceRange;

    @Column(name = "catering_detail", columnDefinition = "TEXT")
    private String cateringDetail;

    @Column(name = "contact")
    private String contact;

    @OneToMany(mappedBy = "event")
    private List<EventPhoto> photos = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<EventDate> eventDates = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<EventCategory> categories = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<EventDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<EventTruck> eventTrucks = new ArrayList<>();

    protected Event() {
    }

    @Builder
    public Event(String createdBy, EventCreatorType creatorType, String name, String description, String guidelines, Boolean isDeleted,
                 EventDocumentSubmissionTarget documentSubmissionTarget, String submissionEmail, EventType type,
                 ExpectedParticipants expectedParticipants, Set<TruckType> truckTypes, SaleType saleType,
                 PriceRange priceRange, String cateringDetail, String contact,
                 String recruitmentUrl, int recruitmentUrlClickCount) {
        this.createdBy = createdBy;
        this.creatorType = creatorType;
        this.name = name;
        this.description = description;
        this.guidelines = guidelines;
        this.isDeleted = isDeleted;
        this.documentSubmissionTarget = documentSubmissionTarget;
        this.submissionEmail = submissionEmail;
        this.type = type;
        this.expectedParticipants = expectedParticipants;
        this.truckTypes = truckTypes != null ? new HashSet<>(truckTypes) : new HashSet<>();
        this.saleType = saleType;
        this.priceRange = priceRange;
        this.cateringDetail = cateringDetail;
        this.contact = contact;
        this.recruitmentUrl = recruitmentUrl;
        this.recruitmentUrlClickCount = recruitmentUrlClickCount;
        this.isHidden = false;
    }

    public void updateBasicInfo(String name, EventType type, ExpectedParticipants expectedParticipants) {
        this.name = name;
        this.type = type;
        this.expectedParticipants = expectedParticipants;
    }

    public void updateDetailInfo(String description, String guidelines, String contact) {
        this.description = description;
        this.guidelines = guidelines;
        this.contact = contact;
    }

    public void updateTargetInfo(Set<TruckType> truckTypes, SaleType saleType, PriceRange priceRange, String cateringDetail) {
        this.truckTypes = truckTypes != null ? new HashSet<>(truckTypes) : new HashSet<>();
        this.saleType = saleType;
        if (saleType == SaleType.NORMAL) {
            this.priceRange = priceRange;
            this.cateringDetail = null;
            return;
        }
        this.priceRange = null;
        this.cateringDetail = cateringDetail;
    }

    public void updateDocumentInfo(String submissionEmail, EventDocumentSubmissionTarget documentSubmissionTarget) {
        this.submissionEmail = submissionEmail;
        this.documentSubmissionTarget = documentSubmissionTarget;
    }

    public void updateRecruitmentUrl(String recruitmentUrl) {
        this.recruitmentUrl = recruitmentUrl;
    }

    public void updateHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public void addRecruitmentUrlClickCount() {
        this.recruitmentUrlClickCount += 1;
    }

    public void initEventRecruitDetail(EventRecruitDetail eventRecruitDetail) {
        this.recruitDetail = eventRecruitDetail;
    }

    public void initEventView(EventView eventView) {
        this.view = eventView;
    }

    public void initEventRegion(EventRegion eventRegion) {
        this.eventRegion = eventRegion;
    }

    public Boolean isCreator(String memberId) {
        return this.createdBy.equals(memberId);
    }

    public void delete() {
        this.name = "(삭제됨) " + this.name;
        this.isDeleted = true;
    }

    public void updateStatus(EventRecruitingStatus status) {
        this.recruitDetail.updateStatus(status);
    }

    public String getEventMainPhotoUrl(ImageManager imageManager) {
        return getEventPhotoFiles().stream()
                .map(file -> imageManager.getPreSignUrl(file.getPath()))
                .findFirst().orElse(null);
    }

    public List<File> getEventPhotoFiles() {
        if (this.photos == null) {
            return new ArrayList<>();
        }
        return photos.stream()
                .sorted(Comparator.comparing(EventPhoto::getCreatedAt))
                .map(EventPhoto::getFile)
                .toList();
    }

    public List<EventDate> getSortedEventDates() {
        if (this.eventDates == null) {
            return new ArrayList<>();
        }
        return eventDates.stream()
                .sorted(Comparator.comparing(EventDate::getCreatedAt))
                .toList();
    }

    public List<EventTruck> getConfirmedEventTrucks() {
        if (this.eventTrucks == null) {
            return new ArrayList<>();
        }
        return this.eventTrucks.stream()
                .filter(truck -> truck.getStatus().equals(EventTruckStatus.CONFIRMED))
                .toList();
    }

    public List<DocumentType> getEventDocumentTypes() {
        return this.documents.stream().map(EventDocument::getType).toList();
    }
}
