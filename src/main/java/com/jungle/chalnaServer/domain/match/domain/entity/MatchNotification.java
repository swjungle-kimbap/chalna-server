package com.jungle.chalnaServer.domain.match.domain.entity;

import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import com.jungle.chalnaServer.global.common.entity.MessageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchNotification extends BaseTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long senderId;
    private Long receiverId;

    private String message;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Enumerated(EnumType.STRING)
    private MatchNotificationStatus status;

    @Column(name = "deleteAt")
    private LocalDateTime deleteAt;

    
    public void updateStatus(MatchNotificationStatus newStatus) {
        this.status = newStatus;
        this.deleteAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public record MESSAGE(String content, MessageType messageType) {

    }

}
