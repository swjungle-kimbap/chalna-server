package com.jungle.chalnaServer.domain.relation.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RelationPK implements Serializable {
    private Long id;

    private Long otherId;
    public RelationPK reverse(){
        return new RelationPK(otherId, id);
    }
}
