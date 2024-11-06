package com.example.backend.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBusinessCategory is a Querydsl query type for BusinessCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBusinessCategory extends EntityPathBase<BusinessCategory> {

    private static final long serialVersionUID = -753685554L;

    public static final QBusinessCategory businessCategory = new QBusinessCategory("businessCategory");

    public final StringPath field = createString("field");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath type = createString("type");

    public QBusinessCategory(String variable) {
        super(BusinessCategory.class, forVariable(variable));
    }

    public QBusinessCategory(Path<? extends BusinessCategory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBusinessCategory(PathMetadata metadata) {
        super(BusinessCategory.class, metadata);
    }

}

