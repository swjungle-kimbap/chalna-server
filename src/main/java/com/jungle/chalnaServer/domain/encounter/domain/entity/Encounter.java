package com.jungle.chalnaServer.domain.encounter.domain.entity;


import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "encounter", indexes = {
        @Index(name = "idx_member_other", columnList = "member_id, other_id")
})
public class Encounter extends BaseTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long otherId;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;


}
