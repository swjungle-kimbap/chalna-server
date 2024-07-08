package com.jungle.chalnaServer.domain.relation.repository;

import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.relation.domain.entity.Relation;
import com.jungle.chalnaServer.domain.relation.domain.entity.RelationPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RelationRepository extends JpaRepository<Relation, RelationPK>, QuerydslPredicateExecutor<Relation> {
    @Query("SELECT r from Relation r WHERE r.relationPK.id = :id")
    List<Relation> findById(@Param("id") Long id);

    @Query("SELECT r from Relation r WHERE r.relationPK.id = :id and r.chatRoom = :chatRoom")
    Optional<Relation> findByIdAndChatRoom(@Param("id") Long id, @Param("chatRoom") ChatRoom chatRoom);

}
