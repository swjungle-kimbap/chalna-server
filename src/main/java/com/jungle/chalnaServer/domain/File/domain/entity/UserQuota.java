package com.jungle.chalnaServer.domain.File.domain.entity;

import com.jungle.chalnaServer.domain.File.exception.MaxUploadCountException;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class UserQuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private Integer monthlyUploadCountLimit;
    private Integer monthlyDownloadCountLimit;
    private Integer usedUploadCount;
    private Integer usedDownloadCount;
    private String monthYear;

    public UserQuota(Member member, String monthYear) {
        this.member = member;
        this.monthYear = monthYear;
        this.monthlyUploadCountLimit = 40; // 기본 값 설정
        this.usedUploadCount = 0; // 기본 값 설정
        this.monthlyDownloadCountLimit = 400;
        this.usedDownloadCount = 0;
    }

    public void incrementUploadCount() {
        if (this.usedUploadCount >= this.monthlyUploadCountLimit) {
            throw new MaxUploadCountException();
        }
        this.usedUploadCount++;
    }

}
