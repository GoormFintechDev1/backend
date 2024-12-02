package com.example.backend.model.POS;

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

    private static final long serialVersionUID = -1193409406L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPosSales posSales = new QPosSales("posSales");

    public final ListPath<OrderItem, QOrderItem> orderItems = this.<OrderItem, QOrderItem>createList("orderItems", OrderItem.class, QOrderItem.class, PathInits.DIRECT2);

    public final EnumPath<com.example.backend.model.enumSet.OrderStatus> orderStatus = createEnum("orderStatus", com.example.backend.model.enumSet.OrderStatus.class);

    public final DateTimePath<java.time.LocalDateTime> orderTime = createDateTime("orderTime", java.time.LocalDateTime.class);

    public final EnumPath<com.example.backend.model.enumSet.PaymentStatus> paymentStatus = createEnum("paymentStatus", com.example.backend.model.enumSet.PaymentStatus.class);

    public final EnumPath<com.example.backend.model.enumSet.PaymentType> paymentType = createEnum("paymentType", com.example.backend.model.enumSet.PaymentType.class);

    public final QPos posId;

    public final NumberPath<Long> posSalesId = createNumber("posSalesId", Long.class);

    public final StringPath productName = createString("productName");

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final NumberPath<java.math.BigDecimal> totalPrice = createNumber("totalPrice", java.math.BigDecimal.class);

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
        this.posId = inits.isInitialized("posId") ? new QPos(forProperty("posId")) : null;
    }

}

