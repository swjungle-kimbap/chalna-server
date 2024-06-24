package com.jungle.chalnaServer.domain.relation.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRelationPK is a Querydsl query type for RelationPK
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QRelationPK extends BeanPath<RelationPK> {

    private static final long serialVersionUID = -534972412L;

    public static final QRelationPK relationPK = new QRelationPK("relationPK");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> otherId = createNumber("otherId", Long.class);

    public QRelationPK(String variable) {
        super(RelationPK.class, forVariable(variable));
    }

    public QRelationPK(Path<? extends RelationPK> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRelationPK(PathMetadata metadata) {
        super(RelationPK.class, metadata);
    }

}

