package com.jungle.chalnaServer.infra.file.domain.entity;

import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@SQLRestriction("is_deleted = true")
public class FileInfo extends BaseTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName;
    @Column(nullable = false)
    private String s3FileName;
    @Column(nullable = false, length = 255)
    private String fileUrl;
    @Column(nullable = false)
    private Long fileSize;
    @Column(nullable = true)
    private Long chatRoomId;
    @Column(nullable = true)
    private String originalFileUrl;

//    @Column(nullable = false)
//    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private Member uploadedBy;

    public void updateOriginalFileUrl(String originalFileUrl) {
        this.originalFileUrl = originalFileUrl;
    }
//    public void softDelete() {
//        this.isDeleted = true;
//    }

}
