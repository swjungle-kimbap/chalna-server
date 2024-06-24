package com.jungle.chalnaServer.domain.settings.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberSetting is a Querydsl query type for MemberSetting
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberSetting extends EntityPathBase<MemberSetting> {

    private static final long serialVersionUID = 1477571024L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberSetting memberSetting = new QMemberSetting("memberSetting");

    public final com.jungle.chalnaServer.global.common.entity.QBaseTimestampEntity _super = new com.jungle.chalnaServer.global.common.entity.QBaseTimestampEntity(this);

    public final BooleanPath alarmSound = createBoolean("alarmSound");

    public final BooleanPath alarmVibration = createBoolean("alarmVibration");

    public final BooleanPath bluetooth = createBoolean("bluetooth");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<String, StringPath> interestTags = this.<String, StringPath>createList("interestTags", String.class, StringPath.class, PathInits.DIRECT2);

    public final BooleanPath isAlarm = createBoolean("isAlarm");

    public final BooleanPath isChatAlarm = createBoolean("isChatAlarm");

    public final BooleanPath isFriendAlarm = createBoolean("isFriendAlarm");

    public final BooleanPath isTagAlarm = createBoolean("isTagAlarm");

    public final com.jungle.chalnaServer.domain.member.domain.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMemberSetting(String variable) {
        this(MemberSetting.class, forVariable(variable), INITS);
    }

    public QMemberSetting(Path<? extends MemberSetting> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberSetting(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberSetting(PathMetadata metadata, PathInits inits) {
        this(MemberSetting.class, metadata, inits);
    }

    public QMemberSetting(Class<? extends MemberSetting> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.jungle.chalnaServer.domain.member.domain.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

