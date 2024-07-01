package com.jungle.chalnaServer.infra.file.domain.entity;

import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false)
    private Member uploadedBy;

}
