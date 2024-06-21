package com.jungle.chalnaServer.domain.match.domain.entity;

import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
    private MatchNotificationStatus status;

    @Column(name = "deleteAt")
    private LocalDateTime deleteAt;

    
    public void updateStatus(MatchNotificationStatus newStatus) {
        this.status = newStatus;
        this.deleteAt = LocalDateTime.now();
    }
}
