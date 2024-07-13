package com.jungle.chalnaServer.domain.friend.domain.entity;

import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Request extends BaseTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long otherId;

    private Long chatRoomId;

    private String username;

    public Request(Long memberId, Long otherId, Long chatRoomId, String username) {}

}
