package com.jungle.chalnaServer.domain.member.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1574826185L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final com.jungle.chalnaServer.global.common.entity.QBaseTimestampEntity _super = new com.jungle.chalnaServer.global.common.entity.QBaseTimestampEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath deviceId = createString("deviceId");

    public final StringPath fcmToken = createString("fcmToken");

    public final DateTimePath<java.time.LocalDateTime> fcmTokenReceivedAt = createDateTime("fcmTokenReceivedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> kakaoId = createNumber("kakaoId", Long.class);

    public final StringPath loginToken = createString("loginToken");

    public final com.jungle.chalnaServer.domain.settings.domain.entity.QMemberSetting memberSetting;

    public final StringPath message = createString("message");

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final StringPath refreshToken = createString("refreshToken");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath username = createString("username");

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.memberSetting = inits.isInitialized("memberSetting") ? new com.jungle.chalnaServer.domain.settings.domain.entity.QMemberSetting(forProperty("memberSetting"), inits.get("memberSetting")) : null;
    }

}

