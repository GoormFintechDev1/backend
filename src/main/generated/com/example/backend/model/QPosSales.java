package com.example.backend.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPosSales is a Querydsl query type for PosSales
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPosSales extends EntityPathBase<PosSales> {

    private static final long serialVersionUID = 263419400L;

    public static final QPosSales posSales = new QPosSales("posSales");

    public final StringPath approvalNumber = createString("approvalNumber");

    public final StringPath cardCompany = createString("cardCompany");

    public final EnumPath<com.example.backend.model.enumSet.PaymentTypeEnum> paymentType = createEnum("paymentType", com.example.backend.model.enumSet.PaymentTypeEnum.class);

    public final NumberPath<Long> posId = createNumber("posId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> saleDate = createDateTime("saleDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> saleId = createNumber("saleId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> saleTime = createDateTime("saleTime", java.time.LocalDateTime.class);

    public final NumberPath<java.math.BigDecimal> totalAmount = createNumber("totalAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> vatAmount = createNumber("vatAmount", java.math.BigDecimal.class);

    public QPosSales(String variable) {
        super(PosSales.class, forVariable(variable));
    }

    public QPosSales(Path<? extends PosSales> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPosSales(PathMetadata metadata) {
        super(PosSales.class, metadata);
    }

}

