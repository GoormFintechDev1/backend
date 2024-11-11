package com.example.backend.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGoals is a Querydsl query type for Goals
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGoals extends EntityPathBase<Goals> {

    private static final long serialVersionUID = -2105848112L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGoals goals = new QGoals("goals");

    public final QBaseTime _super = new QBaseTime(this);

    public final QBusinessRegistration businessId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final BooleanPath expenseAchieved = createBoolean("expenseAchieved");

    public final NumberPath<java.math.BigDecimal> expenseGoal = createNumber("expenseGoal", java.math.BigDecimal.class);

    public final NumberPath<Long> goalId = createNumber("goalId", Long.class);

    public final DatePath<java.time.LocalDate> goalMonth = createDate("goalMonth", java.time.LocalDate.class);

    public final BooleanPath revenueAchieved = createBoolean("revenueAchieved");

    public final NumberPath<java.math.BigDecimal> revenueGoal = createNumber("revenueGoal", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QGoals(String variable) {
        this(Goals.class, forVariable(variable), INITS);
    }

    public QGoals(Path<? extends Goals> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGoals(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGoals(PathMetadata metadata, PathInits inits) {
        this(Goals.class, metadata, inits);
    }

    public QGoals(Class<? extends Goals> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.businessId = inits.isInitialized("businessId") ? new QBusinessRegistration(forProperty("businessId"), inits.get("businessId")) : null;
    }

}

