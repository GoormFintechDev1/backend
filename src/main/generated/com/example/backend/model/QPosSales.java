package com.example.backend.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPosSales is a Querydsl query type for PosSales
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPosSales extends EntityPathBase<PosSales> {

    private static final long serialVersionUID = 263419400L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPosSales posSales = new QPosSales("posSales");

    public final StringPath approvalNumber = createString("approvalNumber");

    public final StringPath cardCompany = createString("cardCompany");

    public final EnumPath<com.example.backend.model.enumSet.PaymentTypeEnum> paymentType = createEnum("paymentType", com.example.backend.model.enumSet.PaymentTypeEnum.class);

    public final QPos pos;

    public final StringPath productName = createString("productName");

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> saleDate = createDateTime("saleDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> saleId = createNumber("saleId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> saleTime = createDateTime("saleTime", java.time.LocalDateTime.class);

    public final NumberPath<java.math.BigDecimal> totalAmount = createNumber("totalAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> vatAmount = createNumber("vatAmount", java.math.BigDecimal.class);

    public QPosSales(String variable) {
        this(PosSales.class, forVariable(variable), INITS);
    }

    public QPosSales(Path<? extends PosSales> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPosSales(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPosSales(PathMetadata metadata, PathInits inits) {
        this(PosSales.class, metadata, inits);
    }

    public QPosSales(Class<? extends PosSales> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pos = inits.isInitialized("pos") ? new QPos(forProperty("pos"), inits.get("pos")) : null;
    }

}

