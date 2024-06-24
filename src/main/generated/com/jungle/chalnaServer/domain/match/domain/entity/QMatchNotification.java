package com.jungle.chalnaServer.domain.match.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMatchNotification is a Querydsl query type for MatchNotification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMatchNotification extends EntityPathBase<MatchNotification> {

    private static final long serialVersionUID = -501772896L;

    public static final QMatchNotification matchNotification = new QMatchNotification("matchNotification");

    public final com.jungle.chalnaServer.global.common.entity.QBaseTimestampEntity _super = new com.jungle.chalnaServer.global.common.entity.QBaseTimestampEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deleteAt = createDateTime("deleteAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath message = createString("message");

    public final NumberPath<Long> receiverId = createNumber("receiverId", Long.class);

    public final NumberPath<Long> senderId = createNumber("senderId", Long.class);

    public final EnumPath<MatchNotificationStatus> status = createEnum("status", MatchNotificationStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMatchNotification(String variable) {
        super(MatchNotification.class, forVariable(variable));
    }

    public QMatchNotification(Path<? extends MatchNotification> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMatchNotification(PathMetadata metadata) {
        super(MatchNotification.class, metadata);
    }

}

