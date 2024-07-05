package com.jungle.chalnaServer.domain.member.domain.entity;

import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import com.jungle.chalnaServer.infra.file.domain.entity.FileInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    private String profileImageUrl = "/images/default_image.png";

    @Column(nullable = false,unique = true)
    private Long kakaoId;

    private String deviceId;

    private String loginToken;

    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FileInfo> fileInfoList;

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateMessage(String message) {
        this.message = message;
    }

    public void updateDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void updateProfileImageUrl(String profileImageUrl) { // 이미지 URL 업데이트 메서드
        this.profileImageUrl = profileImageUrl;
    }
}
