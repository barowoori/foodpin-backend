package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.document.command.domain.model.BusinessRegistration;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.document.command.domain.model.QBusinessRegistration;
import com.barowoori.foodpinbackend.file.command.domain.model.QFile;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruck;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocumentStatus;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.BackOfficeTruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDocumentInfoDto;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDocumentManager;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.barowoori.foodpinbackend.document.command.domain.model.QBusinessRegistration.businessRegistration;
import static com.barowoori.foodpinbackend.member.command.domain.model.QMember.member;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruck.truck;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckDocument.truckDocument;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckDocumentPhoto.truckDocumentPhoto;

public class TruckDocumentRepositoryCustomImpl implements TruckDocumentRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public TruckDocumentRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public TruckDocumentManager getDocumentManager(String truckId) {
        QTruckDocument truckDocument = QTruckDocument.truckDocument;
        List<TruckDocument> documents = jpaQueryFactory.selectFrom(truckDocument)
                .where(truckDocument.truck.id.eq(truckId)).fetch();
        return new TruckDocumentManager(documents);
    }

    @Override
    public BusinessRegistration getBusinessRegistrationDocumentByTruckId(String truckId) {
        QTruckDocument truckDocument = QTruckDocument.truckDocument;
        QBusinessRegistration businessRegistration = QBusinessRegistration.businessRegistration;
        return jpaQueryFactory.select(businessRegistration)
                .from(truckDocument)
                .join(businessRegistration).on(businessRegistration.id.eq(truckDocument.documentId))
                .where(truckDocument.truck.id.eq(truckId)
                        .and(truckDocument.type.eq(DocumentType.BUSINESS_REGISTRATION)))
                .fetchOne();
    }

    @Override
    public Map<String, List<TruckDocumentInfoDto>> getDocumentTypeByTruckIds(List<String> truckIds) {

        QTruckDocument truckDocument = QTruckDocument.truckDocument;
        QTruck truck = QTruck.truck;

        List<Tuple> results = jpaQueryFactory
                .select(truck.id, truckDocument.type, truckDocument.status)
                .from(truckDocument)
                .join(truckDocument.truck, truck)
                .where(truck.id.in(truckIds))
                .fetch();

        return results.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(truck.id),
                        Collectors.mapping(
                                tuple -> new TruckDocumentInfoDto(
                                        tuple.get(truckDocument.type),
                                        tuple.get(truckDocument.status)
                                ),
                                Collectors.toList()
                        )
                ));
    }

    @Override
    public List<TruckDocument> getTruckDocumentFiles(String truckId) {
        QFile file = QFile.file;
        return jpaQueryFactory.selectDistinct(truckDocument)
                .from(truckDocument)
                .leftJoin(truckDocument.photos, truckDocumentPhoto)
                .leftJoin(truckDocumentPhoto.file, file)
                .where(truckDocument.truck.id.eq(truckId))
                .fetch();
    }

    @Override
    public Page<BackOfficeTruckDocument> getBackOfficeTruckDocuments(DocumentType type, String nickname, String phone, TruckDocumentStatus status,
                                                                     LocalDate requestedStartAt, LocalDate requestedEndAt, LocalDate processedStartAt, LocalDate processedEndAt,
                                                                     Pageable pageable) {

        List<BackOfficeTruckDocument> content = jpaQueryFactory
                .select(Projections.constructor(
                        BackOfficeTruckDocument.class,
                        truckDocument,
                        member,
                        businessRegistration
                ))
                .from(truckDocument)
                .leftJoin(truckDocument.truck, truck).fetchJoin()
                .leftJoin(member).on(truckDocument.createdBy.eq(member.id))
                .leftJoin(businessRegistration)
                .on(truckDocument.documentId.eq(businessRegistration.id))
                .where(
                        truckDocument.type.eq(type),
                        nicknameContains(nickname),
                        phoneNumberContains(phone),
                        statusEq(status),
                        requestedBetween(requestedStartAt, requestedEndAt),
                        processedBetween(processedStartAt, processedEndAt)
                )
                .orderBy(truckDocument.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = jpaQueryFactory
                .select(truckDocument.count())
                .from(truckDocument)
                .leftJoin(truckDocument.truck, truck)
                .leftJoin(member).on(truckDocument.createdBy.eq(member.id))
                .where(
                        truckDocument.type.eq(type),
                        nicknameContains(nickname),
                        phoneNumberContains(phone),
                        statusEq(status),
                        requestedBetween(requestedStartAt, requestedEndAt),
                        processedBetween(processedStartAt, processedEndAt)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }

    private BooleanExpression nicknameContains(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return null;
        }
        return member.nickname.contains(nickname);
    }

    private BooleanExpression phoneNumberContains(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }
        return member.phone.contains(phoneNumber);
    }

    private BooleanExpression statusEq(TruckDocumentStatus status) {
        if (status == null) {
            return null;
        }
        return truckDocument.status.eq(status);
    }

    private BooleanExpression requestedBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return null;
        }

        return truckDocument.createdAt.between(
                start.atStartOfDay(),
                end.atTime(LocalTime.MAX)
        );
    }

    private BooleanExpression processedBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return null;
        }

        return truckDocument.updatedAt.between(
                start.atStartOfDay(),
                end.atTime(LocalTime.MAX)
        );
    }

    public Map<String, List<String>> findPhotosByTruckDocumentIds(List<String> documentIds) {
        QFile file = QFile.file;
        List<Tuple> results = jpaQueryFactory
                .select(truckDocumentPhoto.truckDocument.id, file.path)
                .from(truckDocumentPhoto)
                .leftJoin(truckDocumentPhoto.file, file)
                .where(truckDocumentPhoto.truckDocument.id.in(documentIds))
                .fetch();

        return results.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(truckDocumentPhoto.truckDocument.id),
                        Collectors.mapping(
                                tuple -> tuple.get(file.path),
                                Collectors.toList()
                        )
                ));
    }
}
