package com.example.backend.model.POS;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrder is a Querydsl query type for Order
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrder extends EntityPathBase<Order> {

    private static final long serialVersionUID = 229639716L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrder order = new QOrder("order1");

    public final NumberPath<Long> orderId = createNumber("orderId", Long.class);

    public final ListPath<OrderItem, QOrderItem> orderItems = this.<OrderItem, QOrderItem>createList("orderItems", OrderItem.class, QOrderItem.class, PathInits.DIRECT2);

    public final EnumPath<com.example.backend.model.enumSet.OrderStatus> orderStatus = createEnum("orderStatus", com.example.backend.model.enumSet.OrderStatus.class);

    public final DateTimePath<java.time.LocalDateTime> orderTime = createDateTime("orderTime", java.time.LocalDateTime.class);

    public final EnumPath<com.example.backend.model.enumSet.PaymentStatus> paymentStatus = createEnum("paymentStatus", com.example.backend.model.enumSet.PaymentStatus.class);

    public final EnumPath<com.example.backend.model.enumSet.PaymentType> paymentType = createEnum("paymentType", com.example.backend.model.enumSet.PaymentType.class);

    public final QPos posId;

    public final StringPath productName = createString("productName");

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final NumberPath<java.math.BigDecimal> totalPrice = createNumber("totalPrice", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> vatAmount = createNumber("vatAmount", java.math.BigDecimal.class);

    public QOrder(String variable) {
        this(Order.class, forVariable(variable), INITS);
    }

    public QOrder(Path<? extends Order> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrder(PathMetadata metadata, PathInits inits) {
        this(Order.class, metadata, inits);
    }

    public QOrder(Class<? extends Order> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.posId = inits.isInitialized("posId") ? new QPos(forProperty("posId")) : null;
    }

}

