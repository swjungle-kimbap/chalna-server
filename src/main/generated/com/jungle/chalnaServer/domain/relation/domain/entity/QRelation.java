package com.jungle.chalnaServer.domain.relation.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRelation is a Querydsl query type for Relation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRelation extends EntityPathBase<Relation> {

    private static final long serialVersionUID = -1386030007L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRelation relation = new QRelation("relation");

    public final com.jungle.chalnaServer.global.common.entity.QBaseTimestampEntity _super = new com.jungle.chalnaServer.global.common.entity.QBaseTimestampEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final EnumPath<FriendStatus> friendStatus = createEnum("friendStatus", FriendStatus.class);

    public final BooleanPath isBlocked = createBoolean("isBlocked");

    public final DateTimePath<java.time.LocalDateTime> lastOverlapAt = createDateTime("lastOverlapAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> overlapCount = createNumber("overlapCount", Integer.class);

    public final QRelationPK relationPK;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QRelation(String variable) {
        this(Relation.class, forVariable(variable), INITS);
    }

    public QRelation(Path<? extends Relation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRelation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRelation(PathMetadata metadata, PathInits inits) {
        this(Relation.class, metadata, inits);
    }

    public QRelation(Class<? extends Relation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.relationPK = inits.isInitialized("relationPK") ? new QRelationPK(forProperty("relationPK")) : null;
    }

}

