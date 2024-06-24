package com.jungle.chalnaServer.domain.member.domain.entity;

import com.jungle.chalnaServer.domain.settings.domain.entity.MemberSetting;
import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Member extends BaseTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String username;

    private String message;

    @Column(length = 256)
    private String profileImageUrl = "/images/default_image.png";;

    @Column(nullable = false)
    private Long kakaoId;

    private String deviceId;

    private String refreshToken;

    private String fcmToken;

    private LocalDateTime fcmTokenReceivedAt;

    private String loginToken;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MemberSetting memberSetting;

    @PrePersist
    public void prePersist() {
        if (this.memberSetting == null) {
            this.memberSetting = MemberSetting.builder()
                    .member(this)
                    .isAlarm(true)
                    .isFriendAlarm(false)
                    .isChatAlarm(true)
                    .isTagAlarm(false)
                    .interestTags(List.of())
                    .alarmSound(true)
                    .alarmVibration(true)
                    .bluetooth(true)
                    .build();
        }
    }

    public void updateInfo(String loginToken,String deviceId,String fcmToken) {
        this.loginToken = loginToken;
        this.deviceId = deviceId;
        this.fcmToken = fcmToken;
    }

    public void removeInfo() {
        this.deviceId = null;
        this.fcmToken = null;
        this.refreshToken = null;

    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
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
