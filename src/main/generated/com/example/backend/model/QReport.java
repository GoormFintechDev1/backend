package com.example.backend.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReport is a Querydsl query type for Report
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReport extends EntityPathBase<Report> {

    private static final long serialVersionUID = -550646748L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReport report = new QReport("report");

    public final QBaseTime _super = new QBaseTime(this);

    public final NumberPath<Float> bsiIndex = createNumber("bsiIndex", Float.class);

    public final QBusinessRegistration businessRegistrationId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> expenses = createNumber("expenses", Long.class);

    public final NumberPath<Long> industryAvgExpenses = createNumber("industryAvgExpenses", Long.class);

    public final NumberPath<Long> industryAvgProfit = createNumber("industryAvgProfit", Long.class);

    public final NumberPath<Long> industryAvgRevenue = createNumber("industryAvgRevenue", Long.class);

    public final NumberPath<Long> profit = createNumber("profit", Long.class);

    public final NumberPath<Long> reportId = createNumber("reportId", Long.class);

    public final DatePath<java.time.LocalDate> reportMonth = createDate("reportMonth", java.time.LocalDate.class);

    public final NumberPath<Long> revenue = createNumber("revenue", Long.class);

    public final StringPath stabilityRating = createString("stabilityRating");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QReport(String variable) {
        this(Report.class, forVariable(variable), INITS);
    }

    public QReport(Path<? extends Report> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReport(PathMetadata metadata, PathInits inits) {
        this(Report.class, metadata, inits);
    }

    public QReport(Class<? extends Report> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.businessRegistrationId = inits.isInitialized("businessRegistrationId") ? new QBusinessRegistration(forProperty("businessRegistrationId"), inits.get("businessRegistrationId")) : null;
    }

}

