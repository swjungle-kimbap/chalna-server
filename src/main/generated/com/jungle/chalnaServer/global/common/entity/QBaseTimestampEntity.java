package com.jungle.chalnaServer.global.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseTimestampEntity is a Querydsl query type for BaseTimestampEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseTimestampEntity extends EntityPathBase<BaseTimestampEntity> {

    private static final long serialVersionUID = 875235033L;

    public static final QBaseTimestampEntity baseTimestampEntity = new QBaseTimestampEntity("baseTimestampEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QBaseTimestampEntity(String variable) {
        super(BaseTimestampEntity.class, forVariable(variable));
    }

    public QBaseTimestampEntity(Path<? extends BaseTimestampEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseTimestampEntity(PathMetadata metadata) {
        super(BaseTimestampEntity.class, metadata);
    }

}

