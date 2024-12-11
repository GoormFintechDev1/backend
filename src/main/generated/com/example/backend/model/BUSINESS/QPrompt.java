package com.example.backend.model.BUSINESS;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPrompt is a Querydsl query type for Prompt
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPrompt extends EntityPathBase<Prompt> {

    private static final long serialVersionUID = -435445086L;

    public static final QPrompt prompt = new QPrompt("prompt");

    public final StringPath contents = createString("contents");

    public final ComparablePath<java.time.YearMonth> month = createComparable("month", java.time.YearMonth.class);

    public final NumberPath<Long> promptId = createNumber("promptId", Long.class);

    public final StringPath type = createString("type");

    public QPrompt(String variable) {
        super(Prompt.class, forVariable(variable));
    }

    public QPrompt(Path<? extends Prompt> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPrompt(PathMetadata metadata) {
        super(Prompt.class, metadata);
    }

}

