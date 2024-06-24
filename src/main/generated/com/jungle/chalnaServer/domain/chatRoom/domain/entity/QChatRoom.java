package com.jungle.chalnaServer.domain.chatRoom.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatRoom is a Querydsl query type for ChatRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatRoom extends EntityPathBase<ChatRoom> {

    private static final long serialVersionUID = 2011441769L;

    public static final QChatRoom chatRoom = new QChatRoom("chatRoom");

    public final com.jungle.chalnaServer.global.common.entity.QBaseTimestampEntity _super = new com.jungle.chalnaServer.global.common.entity.QBaseTimestampEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> memberCount = createNumber("memberCount", Integer.class);

    public final SetPath<ChatRoomMember, QChatRoomMember> members = this.<ChatRoomMember, QChatRoomMember>createSet("members", ChatRoomMember.class, QChatRoomMember.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> removedAt = createDateTime("removedAt", java.time.LocalDateTime.class);

    public final EnumPath<ChatRoom.ChatRoomType> type = createEnum("type", ChatRoom.ChatRoomType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QChatRoom(String variable) {
        super(ChatRoom.class, forVariable(variable));
    }

    public QChatRoom(Path<? extends ChatRoom> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChatRoom(PathMetadata metadata) {
        super(ChatRoom.class, metadata);
    }

}

