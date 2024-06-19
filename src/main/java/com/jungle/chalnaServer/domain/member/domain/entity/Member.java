package com.jungle.chalnaServer.domain.member.domain.entity;

import com.jungle.chalnaServer.domain.auth.domain.dto.AuthRequest;
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

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateMessage(String message) {
        this.message = message;
    }

    public void updateProfileImageUrl(String profileImageUrl) { // 이미지 URL 업데이트 메서드
        this.profileImageUrl = profileImageUrl;
    }
}
