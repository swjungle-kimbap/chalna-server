package com.jungle.chalnaServer.domain.localchat.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLocalChat is a Querydsl query type for LocalChat
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLocalChat extends EntityPathBase<LocalChat> {

    private static final long serialVersionUID = 2045387825L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLocalChat localChat = new QLocalChat("localChat");

    public final com.jungle.chalnaServer.global.common.entity.QBaseTimestampEntity _super = new com.jungle.chalnaServer.global.common.entity.QBaseTimestampEntity(this);

    public final com.jungle.chalnaServer.domain.chatRoom.domain.entity.QChatRoom chatRoom;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QLocalChat(String variable) {
        this(LocalChat.class, forVariable(variable), INITS);
    }

    public QLocalChat(Path<? extends LocalChat> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLocalChat(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLocalChat(PathMetadata metadata, PathInits inits) {
        this(LocalChat.class, metadata, inits);
    }

    public QLocalChat(Class<? extends LocalChat> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatRoom = inits.isInitialized("chatRoom") ? new com.jungle.chalnaServer.domain.chatRoom.domain.entity.QChatRoom(forProperty("chatRoom")) : null;
    }

}

