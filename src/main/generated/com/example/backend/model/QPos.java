package com.example.backend.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPos is a Querydsl query type for Pos
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPos extends EntityPathBase<Pos> {

    private static final long serialVersionUID = -37936796L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPos pos = new QPos("pos");

    public final QAccount account;

    public final QBusinessRegistration businessRegistration;

    public final NumberPath<java.math.BigDecimal> income = createNumber("income", java.math.BigDecimal.class);

    public final NumberPath<Long> posId = createNumber("posId", Long.class);

    public final ListPath<PosSales, QPosSales> sales = this.<PosSales, QPosSales>createList("sales", PosSales.class, QPosSales.class, PathInits.DIRECT2);

    public QPos(String variable) {
        this(Pos.class, forVariable(variable), INITS);
    }

    public QPos(Path<? extends Pos> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPos(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPos(PathMetadata metadata, PathInits inits) {
        this(Pos.class, metadata, inits);
    }

    public QPos(Class<? extends Pos> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new QAccount(forProperty("account"), inits.get("account")) : null;
        this.businessRegistration = inits.isInitialized("businessRegistration") ? new QBusinessRegistration(forProperty("businessRegistration"), inits.get("businessRegistration")) : null;
    }

}

