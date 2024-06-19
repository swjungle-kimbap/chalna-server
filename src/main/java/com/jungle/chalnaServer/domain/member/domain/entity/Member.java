package com.jungle.chalnaServer.domain.member.domain.entity;

import com.jungle.chalnaServer.domain.member.auth.domain.dto.AuthRequest;
import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Member extends BaseTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String username;

    private String message;

    private String profileImageUrl;

    @Column(nullable = false)
    private Integer kakaoId;

    private String devicedId;

    private String fcmToken;

    private LocalDateTime fcmTokenReceivedAt;

    private String loginToken;



    public void update(AuthRequest dto) {
        this.loginToken = dto.getLoginToken();
        this.devicedId = dto.getDevicedId();
        this.fcmToken = dto.getFcmToken();
    }
}
