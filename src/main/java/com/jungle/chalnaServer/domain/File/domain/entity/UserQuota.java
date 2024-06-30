package com.jungle.chalnaServer.domain.File.domain.entity;

import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class UserQuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    private Integer monthlyUploadCountLimit = 40;
    private Integer monthlyDownloadCountLimit = 400;
    private Integer usedUploadCount = 0;
    private Integer usedDownloadCount = 0;
    private String monthYear;

    public UserQuota(Member member, String monthYear) {
        this.member = member;
        this.monthYear = monthYear;
    }
}
